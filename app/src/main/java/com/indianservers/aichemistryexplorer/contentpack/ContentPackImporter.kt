package com.indianservers.aichemistryexplorer.contentpack

import androidx.room.withTransaction
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest

internal class ContentPackImporter(
    private val database: McqContentDatabase,
    private val validator: ContentPackValidator
) {
    suspend fun import(
        extractedRoot: File,
        manifest: PackManifest,
        catalogPack: CatalogPack,
        sourceZipSha256: String,
        onProgress: (imported: Int, total: Int) -> Unit = { _, _ -> }
    ) {
        validator.validateManifest(manifest, catalogPack)
        val questionsFile = resolveInside(extractedRoot, manifest.questionsFile)
        require(questionsFile.isFile) { "Questions file ${manifest.questionsFile} is missing" }
        val questionsHash = questionsFile.sha256()
        if (!questionsHash.equals(manifest.questionsSha256, ignoreCase = true)) {
            throw ContentValidationException("${manifest.packId}: questions SHA-256 does not match manifest")
        }

        val dao = database.contentDao()
        val seenIds = HashSet<String>(manifest.questionCount * 2)
        var imported = 0

        database.withTransaction {
            dao.deletePack(manifest.packId)
            dao.insertPack(
                McqPackEntity(
                    packId = manifest.packId,
                    subject = manifest.subject,
                    title = manifest.title,
                    language = manifest.language,
                    level = manifest.level,
                    classNumber = manifest.classNumber,
                    version = manifest.version,
                    questionCount = manifest.questionCount,
                    contentLicense = manifest.contentLicense,
                    installedAtEpochMs = System.currentTimeMillis(),
                    sourceSha256 = sourceZipSha256.lowercase()
                )
            )

            val questionBatch = ArrayList<McqQuestionEntity>(IMPORT_BATCH_SIZE)
            val optionBatch = ArrayList<McqOptionEntity>(IMPORT_BATCH_SIZE * 4)
            questionsFile.bufferedReader(Charsets.UTF_8, 64 * 1024).useLines { lines ->
                lines.forEachIndexed { zeroBasedLine, rawLine ->
                    val line = if (zeroBasedLine == 0) rawLine.removePrefix("\uFEFF") else rawLine
                    if (line.isBlank()) return@forEachIndexed
                    if (line.length > MAX_JSONL_LINE_CHARS) {
                        throw ContentValidationException(
                            "${manifest.packId}: line ${zeroBasedLine + 1} exceeds $MAX_JSONL_LINE_CHARS characters"
                        )
                    }
                    val question = try {
                        McqQuestion.fromJson(JSONObject(line))
                    } catch (error: Exception) {
                        throw ContentValidationException(
                            "${manifest.packId}: invalid question at line ${zeroBasedLine + 1}: ${error.message}",
                            error
                        )
                    }
                    validator.validateQuestion(question, manifest, seenIds, extractedRoot)
                    questionBatch += question.toEntity(manifest.packId)
                    question.options.forEachIndexed { index, option ->
                        optionBatch += McqOptionEntity(
                            questionId = question.id,
                            optionId = option.id,
                            text = option.text,
                            explanation = question.optionExplanations.getValue(option.id),
                            displayOrder = index
                        )
                    }
                    imported++
                    if (imported > manifest.questionCount) {
                        throw ContentValidationException("${manifest.packId}: contains more questions than declared")
                    }
                    if (questionBatch.size >= IMPORT_BATCH_SIZE) {
                        dao.insertQuestions(questionBatch)
                        dao.insertOptions(optionBatch)
                        questionBatch.clear()
                        optionBatch.clear()
                        onProgress(imported, manifest.questionCount)
                    }
                }
            }
            if (questionBatch.isNotEmpty()) {
                dao.insertQuestions(questionBatch)
                dao.insertOptions(optionBatch)
            }
            if (imported != manifest.questionCount) {
                throw ContentValidationException(
                    "${manifest.packId}: declared ${manifest.questionCount} questions but imported $imported"
                )
            }
            if (dao.countQuestions(manifest.packId) != manifest.questionCount) {
                throw ContentValidationException("${manifest.packId}: database count verification failed")
            }
            onProgress(imported, manifest.questionCount)
        }
    }

    private fun McqQuestion.toEntity(packId: String) = McqQuestionEntity(
        id = id,
        packId = packId,
        schemaVersion = schemaVersion,
        subject = subject,
        language = language,
        level = level,
        classNumber = classNumber,
        category = category,
        subcategory = subcategory,
        conceptId = conceptId,
        concept = concept,
        difficulty = difficulty,
        cognitiveLevel = cognitiveLevel,
        type = type,
        question = question,
        correctOptionId = correctOptionId,
        explanation = explanation,
        hint = hint,
        formula = formula,
        relatedConceptsJson = JSONArray(relatedConcepts).toString(),
        syllabusReferencesJson = syllabusReferencesJson,
        tagsJson = JSONArray(tags).toString(),
        estimatedSeconds = estimatedSeconds,
        shuffleOptions = shuffleOptions,
        revision = revision,
        mediaPath = mediaPath
    )

    private fun resolveInside(root: File, relative: String): File {
        val canonicalRoot = root.canonicalFile
        val target = File(canonicalRoot, safeRelativePath(relative)).canonicalFile
        if (!target.path.startsWith(canonicalRoot.path + File.separator)) {
            throw ContentValidationException("Unsafe content path: $relative")
        }
        return target
    }

    companion object {
        private const val IMPORT_BATCH_SIZE = 250
        private const val MAX_JSONL_LINE_CHARS = 256_000
    }
}

internal fun File.sha256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    inputStream().buffered(64 * 1024).use { input ->
        val buffer = ByteArray(64 * 1024)
        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            if (read > 0) digest.update(buffer, 0, read)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}
