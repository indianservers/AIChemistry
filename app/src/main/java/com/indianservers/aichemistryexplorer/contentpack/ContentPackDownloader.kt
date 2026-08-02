package com.indianservers.aichemistryexplorer.contentpack

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.UUID
import java.util.zip.ZipInputStream

internal sealed interface PackInstallProgress {
    data object Connecting : PackInstallProgress
    data class Downloading(val downloadedBytes: Long, val totalBytes: Long) : PackInstallProgress
    data object Verifying : PackInstallProgress
    data object Extracting : PackInstallProgress
    data class Importing(val imported: Int, val total: Int) : PackInstallProgress
    data class Complete(val packId: String, val questions: Int) : PackInstallProgress
}

internal class ContentPackDownloader(
    context: Context,
    private val validator: ContentPackValidator,
    private val importer: ContentPackImporter,
    private val allowedHosts: Set<String>
) {
    private val appContext = context.applicationContext
    private val stagingParent = File(appContext.cacheDir, "mcq-content-staging")
    private val persistentAssetsParent = File(appContext.filesDir, "mcq-content-assets")

    suspend fun fetchCatalog(catalogUrl: String): ContentCatalog = withContext(Dispatchers.IO) {
        validateRemoteUrl(catalogUrl)
        val json = requestText(catalogUrl, MAX_CATALOG_BYTES)
        val catalog = try {
            ContentCatalog.fromJson(JSONObject(json))
        } catch (error: Exception) {
            throw ContentValidationException("Catalog JSON is invalid: ${error.message}", error)
        }
        validator.validateCatalog(catalog)
        catalog
    }

    suspend fun installPack(
        catalogPack: CatalogPack,
        onProgress: (PackInstallProgress) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        validator.validateCatalogPack(catalogPack)
        stagingParent.mkdirs()
        val workRoot = File(stagingParent, "${catalogPack.packId}-${UUID.randomUUID()}")
        val zipFile = File(workRoot, "download.zip")
        val extracted = File(workRoot, "extracted")
        var installedAssets: File? = null

        try {
            require(workRoot.mkdirs()) { "Unable to create content staging directory" }
            onProgress(PackInstallProgress.Connecting)
            download(
                url = catalogPack.downloadUrl,
                destination = zipFile,
                expectedBytes = catalogPack.downloadBytes,
                onProgress = { downloaded, total ->
                    onProgress(PackInstallProgress.Downloading(downloaded, total))
                }
            )
            onProgress(PackInstallProgress.Verifying)
            val zipHash = zipFile.sha256()
            if (!zipHash.equals(catalogPack.sha256, ignoreCase = true)) {
                throw ContentValidationException("${catalogPack.packId}: downloaded ZIP SHA-256 does not match catalog")
            }

            onProgress(PackInstallProgress.Extracting)
            safeUnzip(zipFile, extracted)
            val manifestFile = File(extracted, "pack.json")
            if (!manifestFile.isFile) throw ContentValidationException("${catalogPack.packId}: pack.json is missing")
            val manifest = try {
                PackManifest.fromJson(JSONObject(manifestFile.readText(Charsets.UTF_8)))
            } catch (error: Exception) {
                throw ContentValidationException("${catalogPack.packId}: invalid pack.json: ${error.message}", error)
            }
            validator.validateManifest(manifest, catalogPack)

            installedAssets = installAssets(extracted, manifest)
            importer.import(extracted, manifest, catalogPack, zipHash) { imported, total ->
                onProgress(PackInstallProgress.Importing(imported, total))
            }
            onProgress(PackInstallProgress.Complete(manifest.packId, manifest.questionCount))
        } catch (error: Exception) {
            installedAssets?.let(::safeDeleteInsideAssets)
            throw error
        } finally {
            workRoot.deleteRecursively()
        }
    }

    fun resolveMediaFile(packId: String, version: Int, relativeMediaPath: String): File? {
        val relative = safeRelativePath(relativeMediaPath)
        val versionRoot = File(File(persistentAssetsParent, packId), "v$version").canonicalFile
        val media = File(versionRoot, relative.removePrefix("assets/")).canonicalFile
        return media.takeIf {
            it.path.startsWith(versionRoot.path + File.separator) && it.isFile
        }
    }

    private fun requestText(url: String, maxBytes: Long): String {
        val connection = openConnection(url)
        return try {
            ensureSuccess(connection)
            val declared = connection.contentLengthLong
            if (declared > maxBytes) throw ContentValidationException("Server response is larger than allowed")
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                val output = StringBuilder()
                val chars = CharArray(8_192)
                var total = 0L
                while (true) {
                    val read = reader.read(chars)
                    if (read < 0) break
                    total += read
                    if (total > maxBytes) throw ContentValidationException("Server response is larger than allowed")
                    output.append(chars, 0, read)
                }
                output.toString()
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun download(
        url: String,
        destination: File,
        expectedBytes: Long,
        onProgress: (Long, Long) -> Unit
    ) {
        validateRemoteUrl(url)
        if (expectedBytes <= 0 || expectedBytes > MAX_PACK_BYTES) {
            throw ContentValidationException("Catalog contains an invalid pack size")
        }
        val connection = openConnection(url)
        try {
            ensureSuccess(connection)
            validateFinalConnection(connection)
            val declared = connection.contentLengthLong
            if (declared > MAX_PACK_BYTES) throw ContentValidationException("Pack exceeds the maximum download size")
            if (declared > 0 && declared != expectedBytes) {
                throw ContentValidationException("Server Content-Length does not match catalog downloadBytes")
            }
            var downloaded = 0L
            BufferedInputStream(connection.inputStream, 64 * 1024).use { input ->
                FileOutputStream(destination).buffered(64 * 1024).use { output ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        if (read == 0) continue
                        downloaded += read
                        if (downloaded > MAX_PACK_BYTES || downloaded > expectedBytes) {
                            throw ContentValidationException("Downloaded pack is larger than declared")
                        }
                        output.write(buffer, 0, read)
                        onProgress(downloaded, expectedBytes)
                    }
                }
            }
            if (downloaded != expectedBytes) {
                throw ContentValidationException("Downloaded $downloaded bytes; expected $expectedBytes")
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun safeUnzip(zipFile: File, destination: File) {
        require(destination.mkdirs()) { "Unable to create extraction directory" }
        val canonicalRoot = destination.canonicalFile
        var entries = 0
        var extractedBytes = 0L
        ZipInputStream(BufferedInputStream(zipFile.inputStream(), 64 * 1024)).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                entries++
                if (entries > MAX_ZIP_ENTRIES) throw ContentValidationException("ZIP contains too many entries")
                val relative = safeRelativePath(entry.name)
                val output = File(canonicalRoot, relative).canonicalFile
                if (!output.path.startsWith(canonicalRoot.path + File.separator)) {
                    throw ContentValidationException("ZIP entry escapes the extraction directory")
                }
                if (entry.isDirectory) {
                    if (!output.exists() && !output.mkdirs()) throw ContentValidationException("Cannot create ZIP directory")
                } else {
                    output.parentFile?.mkdirs()
                    FileOutputStream(output).buffered(64 * 1024).use { fileOutput ->
                        val buffer = ByteArray(64 * 1024)
                        while (true) {
                            val read = zip.read(buffer)
                            if (read < 0) break
                            if (read == 0) continue
                            extractedBytes += read
                            if (extractedBytes > MAX_EXTRACTED_BYTES) {
                                throw ContentValidationException("Extracted content exceeds the safety limit")
                            }
                            fileOutput.write(buffer, 0, read)
                        }
                    }
                }
                zip.closeEntry()
            }
        }
    }

    private fun installAssets(extractedRoot: File, manifest: PackManifest): File? {
        val assetsRelative = manifest.assetsDirectory ?: return null
        val source = File(extractedRoot, assetsRelative).canonicalFile
        if (!source.exists()) return null
        if (!source.isDirectory) throw ContentValidationException("assetsDirectory must identify a directory")
        val packRoot = File(persistentAssetsParent, manifest.packId)
        val target = File(packRoot, "v${manifest.version}")
        safeDeleteInsideAssets(target)
        target.mkdirs()
        source.copyRecursively(target, overwrite = false)
        packRoot.listFiles()
            ?.filter { it.isDirectory && it.name != target.name }
            ?.forEach(::safeDeleteInsideAssets)
        return target
    }

    private fun safeDeleteInsideAssets(target: File) {
        val canonicalParent = persistentAssetsParent.canonicalFile
        val canonicalTarget = target.canonicalFile
        if (canonicalTarget.path.startsWith(canonicalParent.path + File.separator)) {
            canonicalTarget.deleteRecursively()
        }
    }

    private fun openConnection(url: String): HttpURLConnection {
        validateRemoteUrl(url)
        return (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 20_000
            readTimeout = 45_000
            instanceFollowRedirects = true
            useCaches = false
            setRequestProperty("Accept", "application/json, application/zip, application/octet-stream")
            setRequestProperty("User-Agent", "IndianServers-ContentPack/1")
        }
    }

    private fun ensureSuccess(connection: HttpURLConnection) {
        val status = connection.responseCode
        if (status !in 200..299) throw ContentValidationException("Server returned HTTP $status")
        validateFinalConnection(connection)
    }

    private fun validateFinalConnection(connection: HttpURLConnection) =
        validateRemoteUrl(connection.url.toString())

    private fun validateRemoteUrl(url: String) {
        val uri = runCatching { URI(url) }
            .getOrElse { throw ContentValidationException("Invalid content URL", it) }
        if (uri.scheme != "https" || uri.host?.lowercase() !in allowedHosts) {
            throw ContentValidationException("Only HTTPS URLs on approved content hosts are allowed")
        }
        if (uri.userInfo != null || uri.fragment != null) {
            throw ContentValidationException("Content URL contains unsupported components")
        }
    }

    companion object {
        private const val MAX_CATALOG_BYTES = 2L * 1024 * 1024
        private const val MAX_PACK_BYTES = 100L * 1024 * 1024
        private const val MAX_EXTRACTED_BYTES = 250L * 1024 * 1024
        private const val MAX_ZIP_ENTRIES = 20_000
    }
}
