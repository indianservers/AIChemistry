package com.indianservers.aichemistryexplorer

import androidx.compose.ui.test.*
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class ElementsViewerSwipeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun rightAdvancesLeftReturnsAndHydrogenDoesNotWrap() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("elements_viewer", 0).edit().putInt("selected", 1).commit()
        compose.setContent {
            androidx.compose.runtime.CompositionLocalProvider(LocalReducedMotion provides true) {
                ElementsViewerScreen(completePeriodicTable(emptyList()), {})
            }
        }
        compose.onNodeWithTag("elementPager").performTouchInput { swipe(androidx.compose.ui.geometry.Offset(width * .9f, height * .15f), androidx.compose.ui.geometry.Offset(width * .1f, height * .15f), 300L) }
        compose.waitForIdle()
        compose.onNodeWithText("Element 001 / 118").assertIsDisplayed()
        compose.onNodeWithTag("elementPager").performTouchInput { swipe(androidx.compose.ui.geometry.Offset(width * .1f, height * .15f), androidx.compose.ui.geometry.Offset(width * .9f, height * .15f), 300L) }
        compose.waitForIdle()
        compose.onNodeWithText("Element 002 / 118").assertIsDisplayed()
        compose.onNodeWithTag("elementPager").performTouchInput { swipe(androidx.compose.ui.geometry.Offset(width * .9f, height * .15f), androidx.compose.ui.geometry.Offset(width * .1f, height * .15f), 300L) }
        compose.waitForIdle()
        compose.onNodeWithText("Element 001 / 118").assertIsDisplayed()
        compose.onNodeWithText("Search").performClick()
        compose.onNode(hasSetTextAction()).performTextInput("118")
        compose.onNodeWithText("118  Og  Oganesson").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Element 118 / 118").assertIsDisplayed()
        compose.onNodeWithTag("elementPager").performTouchInput { swipe(androidx.compose.ui.geometry.Offset(width * .1f, height * .15f), androidx.compose.ui.geometry.Offset(width * .9f, height * .15f), 300L) }
        compose.waitForIdle()
        compose.onNodeWithText("Element 118 / 118").assertIsDisplayed()
    }
    @Test fun swipeThroughAll118ElementsInBothDirections() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("elements_viewer", 0).edit().putInt("selected", 1).commit()
        val elements = completePeriodicTable(emptyList())
        compose.setContent {
            androidx.compose.runtime.CompositionLocalProvider(LocalReducedMotion provides true) {
                ElementsViewerScreen(elements, {})
            }
        }
        fun verify(number: Int) {
            compose.onNodeWithText("Element %03d / 118".format(number)).assertIsDisplayed()
            compose.onNode(hasText(elements[number - 1].name) and hasAnyAncestor(hasTestTag("elementCard$number"))).assertIsDisplayed()
        }
        verify(1)
        for (number in 2..118) {
            compose.onNodeWithTag("elementPager").performTouchInput { swipe(androidx.compose.ui.geometry.Offset(width * .1f, height * .15f), androidx.compose.ui.geometry.Offset(width * .9f, height * .15f), 300L) }
            compose.waitForIdle()
            verify(number)
        }
        for (number in 117 downTo 1) {
            compose.onNodeWithTag("elementPager").performTouchInput { swipe(androidx.compose.ui.geometry.Offset(width * .9f, height * .15f), androidx.compose.ui.geometry.Offset(width * .1f, height * .15f), 300L) }
            compose.waitForIdle()
            verify(number)
        }
    }

    @Test fun selectionAndTabsRestoreOnSmallScreenWithLargeText() {
        val restoration = androidx.compose.ui.test.junit4.StateRestorationTester(compose)
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("elements_viewer", 0).edit().putInt("selected", 8).commit()
        restoration.setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                LocalReducedMotion provides true,
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(1f, 1.3f)
            ) {
                androidx.compose.foundation.layout.Box(androidx.compose.ui.Modifier
                    .requiredSize(320.dp, 600.dp)) {
                    ElementsViewerScreen(completePeriodicTable(emptyList()), {})
                }
            }
        }
        compose.onNodeWithText("Element 008 / 118").assertIsDisplayed()
        compose.onNodeWithTag("elementPager").performTouchInput { swipe(androidx.compose.ui.geometry.Offset(width * .1f, height * .15f), androidx.compose.ui.geometry.Offset(width * .9f, height * .15f), 300L) }
        compose.waitForIdle()
        compose.onNode(hasText("Atomic Structure") and hasAnyAncestor(hasTestTag("elementCard9"))).performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNode(hasText("Atomic Structure") and hasAnyAncestor(hasTestTag("elementCard9"))).assertIsSelected()
        restoration.emulateSavedInstanceStateRestore()
        compose.waitForIdle()
        compose.onNodeWithText("Element 009 / 118").assertIsDisplayed()
        compose.onNode(hasText("Atomic Structure") and hasAnyAncestor(hasTestTag("elementCard9"))).assertIsSelected()
        compose.onNode(hasText("Boiling point") and hasAnyAncestor(hasTestTag("elementCard9"))).performScrollTo().assertIsDisplayed()
    }

    @Test fun repeatedSaveAndCategoryChangesDoNotCrash() {
        val prefs = InstrumentationRegistry.getInstrumentation().targetContext.getSharedPreferences("elements_viewer", 0)
        val originalSaved = "8" in prefs.getStringSet("favorites", emptySet()).orEmpty()
        prefs.edit().putInt("selected", 8).commit()
        val elements = completePeriodicTable(emptyList())
        compose.setContent {
            androidx.compose.runtime.CompositionLocalProvider(LocalReducedMotion provides true) {
                ElementsViewerScreen(elements, {})
            }
        }
        repeat(20) { index ->
            compose.onNodeWithTag("saveElement8").performClick()
            compose.waitForIdle()
            val saved = if (index % 2 == 0) !originalSaved else originalSaved
            compose.onNodeWithContentDescription("${if (saved) "Remove" else "Add"} Oxygen favorite").assertIsDisplayed()
            org.junit.Assert.assertEquals(saved, "8" in prefs.getStringSet("favorites", emptySet()).orEmpty())
        }
        listOf("Metals", "Nonmetals", "Metalloids", "Noble Gases", "All Elements").forEach { category ->
            compose.onNodeWithTag("viewerFilters").performScrollToNode(hasText(category))
            compose.onNodeWithText(category).performClick()
            compose.waitForIdle()
            val matches = elements.filter { viewerMatches(it, category, "") }
            compose.onNodeWithText("Element %03d / %03d".format(matches.first().number, matches.size)).assertIsDisplayed()
        }
        compose.onNodeWithContentDescription("Browse favorites").performClick()
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Browse favorites").performClick()
        compose.waitForIdle()
        compose.onNodeWithTag("elementPager").assertIsDisplayed()
    }

}
