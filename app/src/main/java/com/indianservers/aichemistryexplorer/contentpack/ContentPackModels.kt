package com.indianservers.aichemistryexplorer.contentpack

import org.json.JSONArray
import org.json.JSONObject

internal data class ContentCatalog(
    val catalogVersion: Int,
    val subject: String,
    val updatedAt: String,
    val totalQuestions: Int,
    val packs: List<CatalogPack>
) {
    companion object {
        fun fromJson(json: JSONObject): ContentCatalog {
            val packsArray = json.requireArray("packs")
            return ContentCatalog(
                catalogVersion = json.requirePositiveInt("catalogVersion"),
                subject = json.requireText("subject"),
                updatedAt = json.requireText("updatedAt"),
                totalQuestions = json.requireNonNegativeInt("totalQuestions"),
                packs = List(packsArray.length()) { CatalogPack.fromJson(packsArray.getJSONObject(it)) }
            )
        }
    }
}

internal data class CatalogPack(
    val packId: String,
    val title: String,
    val subject: String,
    val level: String,
    val classNumber: Int?,
    val language: String,
    val version: Int,
    val questionCount: Int,
    val downloadUrl: String,
    val downloadBytes: Long,
    val sha256: String
) {
    companion object {
        fun fromJson(json: JSONObject): CatalogPack = CatalogPack(
            packId = json.requireText("packId"),
            title = json.requireText("title"),
            subject = json.optNullableText("subject") ?: "chemistry",
            level = json.requireText("level"),
            classNumber = json.optNullableInt("class"),
            language = json.requireText("language"),
            version = json.requirePositiveInt("version"),
            questionCount = json.requirePositiveInt("questionCount"),
            downloadUrl = json.requireText("downloadUrl"),
            downloadBytes = json.requirePositiveLong("downloadBytes"),
            sha256 = json.requireSha256("sha256")
        )
    }
}

internal data class PackManifest(
    val schemaVersion: Int,
    val packId: String,
    val subject: String,
    val title: String,
    val language: String,
    val level: String,
    val classNumber: Int?,
    val version: Int,
    val questionCount: Int,
    val createdAt: String,
    val minimumAppVersion: Int,
    val questionsFile: String,
    val conceptIndexFile: String?,
    val assetsDirectory: String?,
    val questionsSha256: String,
    val contentLicense: String
) {
    companion object {
        fun fromJson(json: JSONObject): PackManifest = PackManifest(
            schemaVersion = json.requirePositiveInt("schemaVersion"),
            packId = json.requireText("packId"),
            subject = json.requireText("subject"),
            title = json.requireText("title"),
            language = json.requireText("language"),
            level = json.requireText("level"),
            classNumber = json.optNullableInt("class"),
            version = json.requirePositiveInt("version"),
            questionCount = json.requirePositiveInt("questionCount"),
            createdAt = json.requireText("createdAt"),
            minimumAppVersion = json.requirePositiveInt("minimumAppVersion"),
            questionsFile = json.requireSafeRelativePath("questionsFile"),
            conceptIndexFile = json.optNullableText("conceptIndexFile")?.also(::safeRelativePath),
            assetsDirectory = json.optNullableText("assetsDirectory")?.also(::safeRelativePath),
            questionsSha256 = json.requireSha256("questionsSha256"),
            contentLicense = json.requireText("contentLicense")
        )
    }
}

internal data class McqOption(
    val id: String,
    val text: String
)

internal data class McqQuestion(
    val id: String,
    val schemaVersion: Int,
    val subject: String,
    val language: String,
    val level: String,
    val classNumber: Int?,
    val category: String,
    val subcategory: String,
    val conceptId: String,
    val concept: String,
    val difficulty: String,
    val cognitiveLevel: String,
    val type: String,
    val question: String,
    val options: List<McqOption>,
    val correctOptionId: String,
    val explanation: String,
    val optionExplanations: Map<String, String>,
    val hint: String,
    val formula: String?,
    val relatedConcepts: List<String>,
    val syllabusReferencesJson: String,
    val tags: List<String>,
    val estimatedSeconds: Int,
    val shuffleOptions: Boolean,
    val status: String,
    val revision: Int,
    val mediaPath: String?
) {
    companion object {
        fun fromJson(json: JSONObject): McqQuestion {
            val optionsArray = json.requireArray("options")
            val explanationsObject = json.requireObject("optionExplanations")
            val media = json.optJSONObject("media")
            return McqQuestion(
                id = json.requireText("id"),
                schemaVersion = json.requirePositiveInt("schemaVersion"),
                subject = json.requireText("subject"),
                language = json.requireText("language"),
                level = json.requireText("level"),
                classNumber = json.optNullableInt("class"),
                category = json.requireText("category"),
                subcategory = json.requireText("subcategory"),
                conceptId = json.requireText("conceptId"),
                concept = json.requireText("concept"),
                difficulty = json.requireText("difficulty"),
                cognitiveLevel = json.requireText("cognitiveLevel"),
                type = json.requireText("type"),
                question = json.requireText("question"),
                options = List(optionsArray.length()) {
                    val option = optionsArray.getJSONObject(it)
                    McqOption(option.requireText("id"), option.requireText("text"))
                },
                correctOptionId = json.requireText("correctOptionId"),
                explanation = json.requireText("explanation"),
                optionExplanations = explanationsObject.keys().asSequence()
                    .associateWith { explanationsObject.requireText(it) },
                hint = json.requireText("hint"),
                formula = json.optNullableText("formula"),
                relatedConcepts = json.requireStringList("relatedConcepts"),
                syllabusReferencesJson = json.requireArray("syllabusReferences").toString(),
                tags = json.requireStringList("tags"),
                estimatedSeconds = json.requirePositiveInt("estimatedSeconds"),
                shuffleOptions = json.requireBoolean("shuffleOptions"),
                status = json.requireText("status"),
                revision = json.requirePositiveInt("revision"),
                mediaPath = media?.optNullableText("path")?.also(::safeRelativePath)
            )
        }
    }
}

internal fun JSONObject.requireText(name: String): String {
    if (!has(name) || isNull(name)) throw ContentValidationException("$name is required")
    return getString(name).trim().takeIf { it.isNotEmpty() }
        ?: throw ContentValidationException("$name must not be blank")
}

internal fun JSONObject.optNullableText(name: String): String? =
    if (!has(name) || isNull(name)) null else getString(name).trim().takeIf(String::isNotEmpty)

internal fun JSONObject.requirePositiveInt(name: String): Int =
    getInt(name).takeIf { it > 0 } ?: throw ContentValidationException("$name must be positive")

internal fun JSONObject.requireNonNegativeInt(name: String): Int =
    getInt(name).takeIf { it >= 0 } ?: throw ContentValidationException("$name must not be negative")

internal fun JSONObject.requirePositiveLong(name: String): Long =
    getLong(name).takeIf { it > 0 } ?: throw ContentValidationException("$name must be positive")

internal fun JSONObject.optNullableInt(name: String): Int? =
    if (!has(name) || isNull(name)) null else getInt(name)

internal fun JSONObject.requireBoolean(name: String): Boolean {
    if (!has(name) || isNull(name)) throw ContentValidationException("$name is required")
    return getBoolean(name)
}

internal fun JSONObject.requireArray(name: String): JSONArray =
    optJSONArray(name) ?: throw ContentValidationException("$name must be an array")

internal fun JSONObject.requireObject(name: String): JSONObject =
    optJSONObject(name) ?: throw ContentValidationException("$name must be an object")

internal fun JSONObject.requireStringList(name: String): List<String> {
    val array = requireArray(name)
    return List(array.length()) { index ->
        array.getString(index).trim().takeIf(String::isNotEmpty)
            ?: throw ContentValidationException("$name[$index] must not be blank")
    }
}

internal fun JSONObject.requireSha256(name: String): String =
    requireText(name).lowercase().takeIf { it.matches(Regex("[0-9a-f]{64}")) }
        ?: throw ContentValidationException("$name must be a 64-character SHA-256")

internal fun JSONObject.requireSafeRelativePath(name: String): String =
    safeRelativePath(requireText(name))

internal fun safeRelativePath(path: String): String {
    val normalized = path.replace('\\', '/')
    if (
        normalized.startsWith("/") ||
        normalized.contains("../") ||
        normalized == ".." ||
        Regex("^[A-Za-z]:").containsMatchIn(normalized)
    ) {
        throw ContentValidationException("Unsafe relative path: $path")
    }
    return normalized
}

internal class ContentValidationException(message: String, cause: Throwable? = null) :
    IllegalArgumentException(message, cause)
