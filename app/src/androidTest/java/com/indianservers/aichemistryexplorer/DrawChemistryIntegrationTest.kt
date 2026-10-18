package com.indianservers.aichemistryexplorer

import android.content.pm.ActivityInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import com.indianservers.aichemistryexplorer.drawchemistry.*
import kotlinx.coroutines.runBlocking
import org.junit.*

class DrawChemistryIntegrationTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    private fun screenshot(name:String){
        onView(androidx.test.espresso.matcher.ViewMatchers.isRoot()).perform(object:androidx.test.espresso.ViewAction{
            override fun getDescription()="Wait for native rotation and SurfaceView presentation"
            override fun getConstraints():org.hamcrest.Matcher<android.view.View> = androidx.test.espresso.matcher.ViewMatchers.isRoot()
            override fun perform(controller:androidx.test.espresso.UiController,view:android.view.View){controller.loopMainThreadForAtLeast(1200)}
        })
        val instrumentation=InstrumentationRegistry.getInstrumentation()
        val image=instrumentation.uiAutomation.takeScreenshot()!!
        java.io.File(instrumentation.targetContext.getExternalFilesDir(null),name).outputStream().use {image.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)}
        image.recycle()
    }
    @Test fun dashboardRouteSurvivesRecreationAndLandscape(){
        compose.waitUntil(10000){compose.onAllNodesWithText("Draw Chemistry").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Draw Chemistry").performScrollTo().performClick()
        compose.onNodeWithText("Draw 2D").assertIsDisplayed()
        compose.onNodeWithText("Structures",substring=false).performClick()
        compose.onNodeWithText("Water",substring=false).performClick()
        compose.waitUntil(10000){compose.onAllNodesWithText("Water · 3 atoms").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Build 3D").performClick()
        compose.onNodeWithText("PubChem computed conformer · coordinates in Å").assertIsDisplayed()
        onView(isAssignableFrom(ChemGlView::class.java)).perform(swipeLeft(),swipeUp(),swipeRight())
        screenshot("draw-chemistry-portrait-3d.png")
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Draw Chemistry").assertIsDisplayed()
        compose.onNodeWithText("PubChem computed conformer · coordinates in Å").assertIsDisplayed()
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        compose.onNodeWithText("PubChem computed conformer · coordinates in Å").assertIsDisplayed()
        compose.activityRule.scenario.onActivity {it.requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE}
        compose.waitUntil(5000){compose.activity.resources.configuration.orientation==android.content.res.Configuration.ORIENTATION_LANDSCAPE}
        compose.waitForIdle()
        screenshot("draw-chemistry-landscape-3d.png")
        fun assertDockedViewport(tag:String){
            val controls=compose.onNodeWithTag("landscape-controls").fetchSemanticsNode().boundsInRoot
            val viewport=compose.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
            Assert.assertTrue("Controls must not cover the native viewport",controls.right<=viewport.left)
            Assert.assertTrue("Viewport must retain useful height",viewport.height>=120*compose.activity.resources.displayMetrics.density)
        }
        assertDockedViewport("model-viewport")
        compose.onNodeWithText("Draw Chemistry").assertIsDisplayed()
        compose.onNodeWithText("Hide H",substring=false).assertIsDisplayed()
        compose.onNodeWithContentDescription("Reset",substring=false).assertIsDisplayed()
        compose.onNodeWithText("Search",substring=false).performClick()
        compose.onNode(hasSetTextAction()).assertIsDisplayed()
        compose.onNodeWithText("Hide search").performClick()
        compose.onNodeWithText("Draw 2D").performClick()
        compose.onNodeWithText("Erase",substring=false).assertIsDisplayed()
        screenshot("draw-chemistry-landscape-2d.png")
        assertDockedViewport("drawing-viewport")
        compose.onNodeWithText("Structures",substring=false).performClick()
        compose.onNodeWithText("Water",substring=false).assertIsDisplayed()
        screenshot("draw-chemistry-landscape-library.png")
        compose.onNodeWithText("Properties",substring=false).performClick()
        compose.onNodeWithText("Molecular formula",substring=false).performScrollTo().assertIsDisplayed()
        val propertyBounds=compose.onNodeWithTag("property-list").fetchSemanticsNode().boundsInRoot
        Assert.assertTrue("Property list must have room to scroll",propertyBounds.height>=100*compose.activity.resources.displayMetrics.density)
        screenshot("draw-chemistry-landscape-properties.png")
        compose.onNodeWithText("Spectra",substring=false).performClick()
        compose.onNodeWithText("Not included in this offline compound record.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Overview",substring=false).performClick()
        compose.onNodeWithText("Build 3D").performClick()
        compose.activityRule.scenario.onActivity {it.requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT}
        compose.waitUntil(5000){compose.activity.resources.configuration.orientation==android.content.res.Configuration.ORIENTATION_PORTRAIT}
        compose.waitForIdle()
        compose.onNodeWithText("Properties").performClick()
        compose.waitUntil(5000){compose.onAllNodesWithText("H2O",substring=false).fetchSemanticsNodes().isNotEmpty()}
        compose.onAllNodesWithText("H2O",substring=false).onFirst().assertIsDisplayed()
        compose.onNodeWithContentDescription("Back",substring=false).performClick()
        compose.waitUntil(5000){compose.onAllNodesWithText("AI Chemistry Explorer").fetchSemanticsNodes().isNotEmpty()}
    }
    @Test fun referenceLayoutsShowRealBenzoicAcidAcrossAllFourTabs(){
        compose.waitUntil(10000){compose.onAllNodesWithText("Draw Chemistry").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Draw Chemistry").performScrollTo().performClick()
        compose.onNodeWithText("Structures",substring=false).performClick()
        compose.onNode(hasSetTextAction()).performTextInput("Benzoic")
        compose.onNodeWithText("Benzoic acid",substring=false).performClick()
        compose.onNode(hasSetTextAction()).performTextClearance()
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Select",substring=false).performClick()
        compose.waitUntil(5000){compose.onAllNodesWithText("Benzoic acid · 15 atoms",substring=false).fetchSemanticsNodes().isNotEmpty()}
        screenshot("draw-reference-2d.png")
        compose.onNodeWithText("Build 3D").performClick()
        screenshot("draw-reference-3d.png")
        compose.onNodeWithText("Zoom",substring=false).performClick()
        onView(isAssignableFrom(ChemGlView::class.java)).perform(swipeUp())
        compose.onNodeWithText("Rotate",substring=false).performClick()
        onView(isAssignableFrom(ChemGlView::class.java)).perform(swipeLeft())
        compose.onNodeWithContentDescription("Model settings").performClick()
        compose.onNodeWithText("Close",substring=false).performClick()
        compose.onNodeWithText("Structures",substring=false).performClick()
        compose.onNodeWithText("Water",substring=false).assertIsDisplayed()
        compose.onNodeWithText("Ethanol",substring=false).assertIsDisplayed()
        screenshot("draw-reference-library.png")
        compose.onNodeWithText("Properties",substring=false).performClick()
        compose.onAllNodesWithText("C7H6O2",substring=false).onFirst().assertIsDisplayed()
        screenshot("draw-reference-properties.png")
        compose.onNodeWithText("Spectra",substring=false).performClick()
        compose.onNodeWithText("Not included in this offline compound record.").assertIsDisplayed()
        compose.onNodeWithText("Overview",substring=false).performClick()
        compose.onNodeWithContentDescription("Molecule actions",substring=false).performClick()
        compose.onNodeWithText("Check chemistry",substring=false).performScrollTo().performClick()
        compose.onNodeWithText("Validation",substring=false).assertIsDisplayed()
        compose.onNodeWithText("Overview",substring=false).performClick()
    }
}
