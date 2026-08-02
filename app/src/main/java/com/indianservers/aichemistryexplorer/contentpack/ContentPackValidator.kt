package com.indianservers.aichemistryexplorer.contentpack

import java.io.File
import java.net.URI

internal class ContentPackValidator(
    private val expectedSubject: String,
    private val allowedHosts: Set<String>,
    private val supportedSchemaVersion: Int = 1,
    private val appVersion: Int = 1
) {
    private val allowedLevels = setOf("school", "undergraduate", "postgraduate")
    private val allowedDifficulties = setOf("foundational", "intermediate", "advanced", "challenge")
    private val allowedCognitiveLevels = setOf(
        "recall",
        "understanding",
        "application",
        "analysis",
        "calculation",
        "data_interpretation",
        "experimental_reasoning",
        "mechanism",
        "advanced_reasoning"
    )

    fun validateCatalog(catalog: ContentCatalog) {
        requireContent(catalog.catalogVersion == supportedSchemaVersion) {
            "Unsupported catalog schema ${catalog.catalogVersion}"
        }
        requireContent(catalog.subject == expectedSubject) {
            "Catalog subject ${catalog.subject} does not match $expectedSubject"
        }
        requireContent(catalog.packs.isNotEmpty()) { "Catalog must contain at least one pack" }
        requireContent(catalog.packs.map { it.packId }.distinct().size == catalog.packs.size) {
            "Catalog contains duplicate pack IDs"
        }
        requireContent(catalog.packs.sumOf { it.questionCount } == catalog.totalQuestions) {
            "Catalog totalQuestions does not match the sum of its packs"
        }
        catalog.packs.forEach(::validateCatalogPack)
    }

    fun validateCatalogPack(pack: CatalogPack) {
        requireContent(pack.subject == expectedSubject) { "${pack.packId}: subject mismatch" }
        requireContent(pack.level in allowedLevels) { "${pack.packId}: invalid level ${pack.level}" }
        requireContent(pack.packId.matches(Regex("[a-z0-9][a-z0-9-]{4,79}"))) {
            "${pack.packId}: invalid pack ID"
        }
        requireContent(pack.language.matches(Regex("[a-z]{2}(-[A-Z]{2})?"))) {
            "${pack.packId}: invalid language code"
        }
        validateLevelAndClass(pack.level, pack.classNumber, pack.packId)
        val uri = runCatching { URI(pack.downloadUrl) }
            .getOrElse { throw ContentValidationException("${pack.packId}: invalid download URL", it) }
        requireContent(uri.scheme == "https") { "${pack.packId}: only HTTPS downloads are allowed" }
        requireContent(uri.host?.lowercase() in allowedHosts) {
            "${pack.packId}: download host is not allow-listed"
        }
    }

    fun validateManifest(manifest: PackManifest, catalogPack: CatalogPack) {
        requireContent(manifest.schemaVersion == supportedSchemaVersion) {
            "${manifest.packId}: unsupported schema ${manifest.schemaVersion}"
        }
        requireContent(manifest.minimumAppVersion <= appVersion) {
            "${manifest.packId}: requires app version ${manifest.minimumAppVersion}"
        }
        requireContent(manifest.packId == catalogPack.packId) { "Manifest pack ID does not match catalog" }
        requireContent(manifest.subject == expectedSubject && manifest.subject == catalogPack.subject) {
            "${manifest.packId}: subject mismatch"
        }
        requireContent(manifest.version == catalogPack.version) { "${manifest.packId}: version mismatch" }
        requireContent(manifest.questionCount == catalogPack.questionCount) {
            "${manifest.packId}: question count mismatch"
        }
        requireContent(manifest.level == catalogPack.level && manifest.classNumber == catalogPack.classNumber) {
            "${manifest.packId}: level or class mismatch"
        }
        requireContent(manifest.language == catalogPack.language) { "${manifest.packId}: language mismatch" }
        validateLevelAndClass(manifest.level, manifest.classNumber, manifest.packId)
    }

    fun validateQuestion(
        question: McqQuestion,
        manifest: PackManifest,
        seenIds: MutableSet<String>,
        extractedRoot: File
    ) {
        requireContent(question.schemaVersion == supportedSchemaVersion) {
            "${question.id}: unsupported schema ${question.schemaVersion}"
        }
        requireContent(question.id.matches(Regex("[a-z0-9][a-z0-9-]{5,119}"))) {
            "${question.id}: invalid question ID"
        }
        requireContent(seenIds.add(question.id)) { "${question.id}: duplicate question ID" }
        requireContent(question.subject == expectedSubject && question.subject == manifest.subject) {
            "${question.id}: subject mismatch"
        }
        requireContent(question.language == manifest.language) { "${question.id}: language mismatch" }
        requireContent(question.level == manifest.level && question.classNumber == manifest.classNumber) {
            "${question.id}: level or class does not match its pack"
        }
        validateLevelAndClass(question.level, question.classNumber, question.id)
        requireContent(question.difficulty in allowedDifficulties) {
            "${question.id}: invalid difficulty ${question.difficulty}"
        }
        requireContent(question.cognitiveLevel in allowedCognitiveLevels) {
            "${question.id}: invalid cognitive level ${question.cognitiveLevel}"
        }
        requireContent(question.type == "single_choice") {
            "${question.id}: only single_choice is supported in schema v1"
        }
        requireContent(question.question.length in 12..1_500) {
            "${question.id}: question length must be between 12 and 1500"
        }
        requireContent(question.options.size == 4) { "${question.id}: exactly four options are required" }
        requireContent(question.options.map { it.id }.distinct().size == 4) {
            "${question.id}: option IDs must be unique"
        }
        requireContent(question.options.all { it.text.length in 1..700 }) {
            "${question.id}: option text is blank or too long"
        }
        requireContent(question.options.any { it.id == question.correctOptionId }) {
            "${question.id}: correctOptionId does not identify an option"
        }
        requireContent(question.explanation.length in 40..4_000) {
            "${question.id}: explanation must contain 40–4000 characters"
        }
        requireContent(question.hint.length in 8..1_000) {
            "${question.id}: hint must contain 8–1000 characters"
        }
        requireContent(question.relatedConcepts.size >= 2) {
            "${question.id}: at least two related concepts are required"
        }
        requireContent(question.tags.isNotEmpty()) { "${question.id}: at least one tag is required" }
        requireContent(question.estimatedSeconds in 10..3_600) {
            "${question.id}: estimatedSeconds is outside the accepted range"
        }
        requireContent(question.status == "published") { "${question.id}: only published content may be imported" }
        question.options.forEach { option ->
            val explanation = question.optionExplanations[option.id]
            requireContent(!explanation.isNullOrBlank() && explanation.length >= 12) {
                "${question.id}: option ${option.id} needs an explanation of at least 12 characters"
            }
        }
        question.mediaPath?.let { relative ->
            val root = extractedRoot.canonicalFile
            val media = File(root, relative).canonicalFile
            requireContent(media.path.startsWith(root.path + File.separator) && media.isFile) {
                "${question.id}: referenced media is missing or unsafe"
            }
        }
    }

    private fun validateLevelAndClass(level: String, classNumber: Int?, owner: String) {
        requireContent(level in allowedLevels) { "$owner: invalid level $level" }
        if (level == "school") {
            requireContent(classNumber in 6..12) { "$owner: school packs require class 6–12" }
        } else {
            requireContent(classNumber == null) { "$owner: higher-education packs must not set class" }
        }
    }

    private inline fun requireContent(condition: Boolean, lazyMessage: () -> String) {
        if (!condition) throw ContentValidationException(lazyMessage())
    }
}
