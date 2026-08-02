package com.indianservers.aichemistryexplorer.contentpack

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.File

class ContentPackValidatorTest {
    private val validator = ContentPackValidator(
        expectedSubject = "chemistry",
        allowedHosts = setOf("www.indianservers.com", "indianservers.com")
    )

    @Test
    fun validCatalogIsAccepted() {
        val catalog = ContentCatalog.fromJson(JSONObject(validCatalogJson))
        validator.validateCatalog(catalog)
        assertEquals(1, catalog.packs.size)
        assertEquals("chemistry-class-06-en", catalog.packs.single().packId)
    }

    @Test
    fun nonHttpsOrUnknownHostsAreRejected() {
        val changed = validCatalogJson
            .replace("https://www.indianservers.com", "http://malicious.example")
        val catalog = ContentCatalog.fromJson(JSONObject(changed))
        assertThrows(ContentValidationException::class.java) {
            validator.validateCatalog(catalog)
        }
    }

    @Test
    fun questionRequiresExactlyFourExplainedOptions() {
        val manifest = PackManifest.fromJson(JSONObject(validManifestJson))
        val catalogPack = ContentCatalog.fromJson(JSONObject(validCatalogJson)).packs.single()
        validator.validateManifest(manifest, catalogPack)
        val question = McqQuestion.fromJson(JSONObject(validQuestionJson))
        validator.validateQuestion(question, manifest, mutableSetOf(), File("."))

        val invalid = McqQuestion.fromJson(
            JSONObject(validQuestionJson).apply {
                getJSONArray("options").remove(3)
            }
        )
        assertThrows(ContentValidationException::class.java) {
            validator.validateQuestion(invalid, manifest, mutableSetOf(), File("."))
        }
    }

    private companion object {
        private val hash = "a".repeat(64)
        private val validCatalogJson = """
            {
              "catalogVersion": 1,
              "subject": "chemistry",
              "updatedAt": "2026-08-01T00:00:00Z",
              "totalQuestions": 1,
              "packs": [{
                "packId": "chemistry-class-06-en",
                "title": "Class 6 Chemistry",
                "subject": "chemistry",
                "level": "school",
                "class": 6,
                "language": "en",
                "version": 1,
                "questionCount": 1,
                "downloadUrl": "https://www.indianservers.com/edtech/chemistrymcqs/packs/class-06-en-v1.zip",
                "downloadBytes": 100,
                "sha256": "$hash"
              }]
            }
        """.trimIndent()

        private val validManifestJson = """
            {
              "schemaVersion": 1,
              "packId": "chemistry-class-06-en",
              "subject": "chemistry",
              "title": "Class 6 Chemistry",
              "language": "en",
              "level": "school",
              "class": 6,
              "version": 1,
              "questionCount": 1,
              "createdAt": "2026-08-01T00:00:00Z",
              "minimumAppVersion": 1,
              "questionsFile": "questions.jsonl",
              "conceptIndexFile": "concept-index.json",
              "assetsDirectory": "assets",
              "questionsSha256": "$hash",
              "contentLicense": "Indian Servers educational content"
            }
        """.trimIndent()

        private val validQuestionJson = """
            {
              "id": "chem-c06-matter-0001",
              "schemaVersion": 1,
              "subject": "chemistry",
              "language": "en",
              "level": "school",
              "class": 6,
              "category": "Foundations",
              "subcategory": "Matter",
              "conceptId": "states-of-matter",
              "concept": "States of matter",
              "difficulty": "foundational",
              "cognitiveLevel": "understanding",
              "type": "single_choice",
              "question": "Which state of matter has a fixed volume but takes the shape of its container?",
              "options": [
                {"id": "a", "text": "Solid"},
                {"id": "b", "text": "Liquid"},
                {"id": "c", "text": "Gas"},
                {"id": "d", "text": "Plasma"}
              ],
              "correctOptionId": "b",
              "explanation": "A liquid keeps nearly constant volume, while its particles move past one another so it adopts the shape of its container.",
              "optionExplanations": {
                "a": "A solid normally has both fixed shape and fixed volume.",
                "b": "Correct. A liquid has fixed volume but no fixed shape.",
                "c": "A gas has neither fixed shape nor fixed volume.",
                "d": "Plasma behaves more like an ionized gas and has no fixed volume."
              },
              "hint": "Think about water placed in differently shaped vessels.",
              "formula": null,
              "relatedConcepts": ["particle-model", "physical-properties"],
              "syllabusReferences": [{"framework": "NCERT", "level": "Class 6", "unit": "Materials"}],
              "tags": ["matter", "liquid"],
              "estimatedSeconds": 45,
              "shuffleOptions": true,
              "status": "published",
              "revision": 1
            }
        """.trimIndent()
    }
}
