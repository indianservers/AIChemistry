package com.indianservers.aichemistryexplorer.contentpack

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.Flow
import java.io.IOException

internal data class ContentPackConfig(
    val subject: String,
    val catalogUrl: String,
    val allowedHosts: Set<String>
)

internal object ChemistryMcqContent {
    val config = ContentPackConfig(
        subject = "chemistry",
        catalogUrl = "https://www.indianservers.com/edtech/chemistrymcqs/catalog.json",
        allowedHosts = setOf("indianservers.com", "www.indianservers.com")
    )
}

class ContentPackServices internal constructor(context: Context, config: ContentPackConfig) {
    internal val database = McqContentDatabase.get(context)
    internal val validator = ContentPackValidator(
        expectedSubject = config.subject,
        allowedHosts = config.allowedHosts
    )
    internal val importer = ContentPackImporter(database, validator)
    internal val downloader = ContentPackDownloader(context, validator, importer, config.allowedHosts)
}

internal class ContentPackRepository(
    private val context: Context,
    private val config: ContentPackConfig,
    private val services: ContentPackServices
) {
    suspend fun refreshCatalog(): ContentCatalog = services.downloader.fetchCatalog(config.catalogUrl)

    fun observeInstalledPacks(): Flow<List<McqPackEntity>> =
        services.database.contentDao().observeInstalledPacks(config.subject)

    suspend fun installedVersion(packId: String): Int? =
        services.database.contentDao().getPack(packId)?.version

    fun enqueueDownload(pack: CatalogPack): Flow<WorkInfo?> {
        services.validator.validateCatalogPack(pack)
        val request = OneTimeWorkRequestBuilder<ContentPackDownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .setInputData(pack.toWorkerData())
            .addTag(DOWNLOAD_TAG)
            .addTag("mcq-pack:${pack.packId}")
            .build()
        val uniqueName = "mcq-pack-download:${pack.packId}"
        WorkManager.getInstance(context).enqueueUniqueWork(uniqueName, ExistingWorkPolicy.REPLACE, request)
        return WorkManager.getInstance(context).getWorkInfoByIdFlow(request.id)
    }

    suspend fun selectQuiz(
        level: String? = null,
        classNumber: Int? = null,
        category: String? = null,
        subcategory: String? = null,
        conceptId: String? = null,
        difficulty: String? = null,
        limit: Int = 10
    ): List<StoredQuestion> {
        require(limit in 1..100) { "Quiz limit must be between 1 and 100" }
        return services.database.contentDao().selectQuiz(
            subject = config.subject,
            level = level,
            classNumber = classNumber,
            category = category,
            subcategory = subcategory,
            conceptId = conceptId,
            difficulty = difficulty,
            limit = limit
        )
    }

    suspend fun removePack(packId: String) {
        services.database.contentDao().deletePack(packId)
    }

    private fun CatalogPack.toWorkerData(): Data = Data.Builder()
        .putString(ContentPackDownloadWorker.KEY_PACK_ID, packId)
        .putString(ContentPackDownloadWorker.KEY_TITLE, title)
        .putString(ContentPackDownloadWorker.KEY_SUBJECT, subject)
        .putString(ContentPackDownloadWorker.KEY_LEVEL, level)
        .putInt(ContentPackDownloadWorker.KEY_CLASS, classNumber ?: NO_CLASS)
        .putString(ContentPackDownloadWorker.KEY_LANGUAGE, language)
        .putInt(ContentPackDownloadWorker.KEY_VERSION, version)
        .putInt(ContentPackDownloadWorker.KEY_QUESTION_COUNT, questionCount)
        .putString(ContentPackDownloadWorker.KEY_URL, downloadUrl)
        .putLong(ContentPackDownloadWorker.KEY_BYTES, downloadBytes)
        .putString(ContentPackDownloadWorker.KEY_SHA256, sha256)
        .build()

    companion object {
        const val DOWNLOAD_TAG = "mcq-content-download"
        const val NO_CLASS = -1
    }
}

internal class ContentPackDownloadWorker(
    appContext: Context,
    parameters: WorkerParameters
) : CoroutineWorker(appContext, parameters) {
    override suspend fun doWork(): Result {
        val application = applicationContext as? ContentPackServiceOwner
            ?: return Result.failure(errorData("Application does not provide content-pack services"))
        val pack = runCatching { inputData.toCatalogPack() }
            .getOrElse { return Result.failure(errorData(it.message ?: "Invalid worker input")) }
        return try {
            application.contentPackServices.downloader.installPack(pack) { progress ->
                setProgressAsync(progress.toData())
            }
            Result.success(
                Data.Builder()
                    .putString(KEY_PACK_ID, pack.packId)
                    .putInt(KEY_QUESTION_COUNT, pack.questionCount)
                    .build()
            )
        } catch (error: IOException) {
            if (runAttemptCount < 3) Result.retry()
            else Result.failure(errorData(error.message ?: "Network download failed"))
        } catch (error: Exception) {
            Result.failure(errorData(error.message ?: "Content pack validation failed"))
        }
    }

    private fun Data.toCatalogPack(): CatalogPack = CatalogPack(
        packId = requireNotNull(getString(KEY_PACK_ID)),
        title = requireNotNull(getString(KEY_TITLE)),
        subject = requireNotNull(getString(KEY_SUBJECT)),
        level = requireNotNull(getString(KEY_LEVEL)),
        classNumber = getInt(KEY_CLASS, ContentPackRepository.NO_CLASS).takeIf { it >= 0 },
        language = requireNotNull(getString(KEY_LANGUAGE)),
        version = getInt(KEY_VERSION, 0),
        questionCount = getInt(KEY_QUESTION_COUNT, 0),
        downloadUrl = requireNotNull(getString(KEY_URL)),
        downloadBytes = getLong(KEY_BYTES, 0),
        sha256 = requireNotNull(getString(KEY_SHA256))
    )

    private fun PackInstallProgress.toData(): Data {
        val builder = Data.Builder()
        when (this) {
            PackInstallProgress.Connecting -> builder.putString(KEY_STAGE, "connecting")
            is PackInstallProgress.Downloading -> builder
                .putString(KEY_STAGE, "downloading")
                .putLong(KEY_DOWNLOADED_BYTES, downloadedBytes)
                .putLong(KEY_BYTES, totalBytes)
            PackInstallProgress.Verifying -> builder.putString(KEY_STAGE, "verifying")
            PackInstallProgress.Extracting -> builder.putString(KEY_STAGE, "extracting")
            is PackInstallProgress.Importing -> builder
                .putString(KEY_STAGE, "importing")
                .putInt(KEY_IMPORTED, imported)
                .putInt(KEY_QUESTION_COUNT, total)
            is PackInstallProgress.Complete -> builder
                .putString(KEY_STAGE, "complete")
                .putString(KEY_PACK_ID, packId)
                .putInt(KEY_QUESTION_COUNT, questions)
        }
        return builder.build()
    }

    private fun errorData(message: String): Data =
        Data.Builder().putString(KEY_ERROR, message.take(1_000)).build()

    companion object {
        const val KEY_PACK_ID = "pack_id"
        const val KEY_TITLE = "title"
        const val KEY_SUBJECT = "subject"
        const val KEY_LEVEL = "level"
        const val KEY_CLASS = "class_number"
        const val KEY_LANGUAGE = "language"
        const val KEY_VERSION = "version"
        const val KEY_QUESTION_COUNT = "question_count"
        const val KEY_URL = "download_url"
        const val KEY_BYTES = "download_bytes"
        const val KEY_SHA256 = "sha256"
        const val KEY_STAGE = "stage"
        const val KEY_DOWNLOADED_BYTES = "downloaded_bytes"
        const val KEY_IMPORTED = "imported"
        const val KEY_ERROR = "error"
    }
}

interface ContentPackServiceOwner {
    val contentPackServices: ContentPackServices
}
