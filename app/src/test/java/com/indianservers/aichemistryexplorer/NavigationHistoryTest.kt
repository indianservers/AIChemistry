package com.indianservers.aichemistryexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationHistoryTest {

    @Test
    fun backWalksThroughActualVisitHistoryThenHome() {
        var state = pushNavigation(Screen.Home, emptyList(), Screen.Explore)
        state = pushNavigation(state.screen, state.history, Screen.ModuleLesson)
        state = pushNavigation(state.screen, state.history, Screen.TopicLesson)

        state = popNavigation(state.history)
        assertEquals(Screen.ModuleLesson, state.screen)
        state = popNavigation(state.history)
        assertEquals(Screen.Explore, state.screen)
        state = popNavigation(state.history)
        assertEquals(Screen.Home, state.screen)
        assertTrue(state.history.isEmpty())
    }

    @Test
    fun homeClearsHistoryAndRepeatedDestinationIsNotDuplicated() {
        val first = pushNavigation(Screen.Home, emptyList(), Screen.Table)
        val repeated = pushNavigation(first.screen, first.history, Screen.Table)
        assertEquals(first, repeated)

        val home = homeNavigation()
        assertEquals(Screen.Home, home.screen)
        assertTrue(home.history.isEmpty())
    }
}
