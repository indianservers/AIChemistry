package com.indianservers.aichemistryexplorer

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.*
import org.junit.Test

class LessonContentDeviceTest {
    @Test fun allStoredSchoolPagesRefreshToLessonSpecificContent() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        withContext(Dispatchers.IO) {
            val repository = LessonPageRepository(context)
            repository.replaceAllClassSixToTenLessons()
            val audits = repository.observeSchoolLessonAudits().first()
            assertEquals(curriculum.filter { it.level.startsWith("Class") }.sumOf { it.lessons.size }, audits.size)
            assertTrue(audits.filterNot { it.isReady }.joinToString { "${it.moduleTitle}/${it.lessonTitle}: ${it.issues}" }, audits.all { it.isReady })
            val nuclear = repository.observeLessonPages(lessonPageId("Nuclear Chemistry", "Nuclear equations")).first()
            assertEquals(5, nuclear.size)
            assertTrue(nuclear.any { it.pageContent.contains("238U") && it.pageContent.contains("234Th") })
            assertFalse(nuclear.any { it.pageContent.contains("simple observation from daily life") })
            val water = repository.observeLessonPages(lessonPageId("Air, Water and Environment", "Water cycle and purification")).first()
            assertTrue(water.any { it.pageContent.contains("filtrate") && it.pageContent.contains("salt") })
        }
    }
}
