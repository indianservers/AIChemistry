package com.indianservers.aichemistryexplorer

import androidx.compose.ui.test.*
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
        compose.onNodeWithTag("elementPager").performTouchInput { swipeLeft() }
        compose.waitForIdle()
        compose.onNodeWithText("Element 001 / 118").assertIsDisplayed()
        compose.onNodeWithTag("elementPager").performTouchInput { swipeRight() }
        compose.waitForIdle()
        compose.onNodeWithText("Element 002 / 118").assertIsDisplayed()
        compose.onNodeWithTag("elementPager").performTouchInput { swipeLeft() }
        compose.waitForIdle()
        compose.onNodeWithText("Element 001 / 118").assertIsDisplayed()
        compose.onNodeWithText("Search").performClick()
        compose.onNode(hasSetTextAction()).performTextInput("118")
        compose.onNodeWithText("118  Og  Oganesson").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Element 118 / 118").assertIsDisplayed()
        compose.onNodeWithTag("elementPager").performTouchInput { swipeRight() }
        compose.waitForIdle()
        compose.onNodeWithText("Element 118 / 118").assertIsDisplayed()
    }
}
