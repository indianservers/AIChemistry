package com.indianservers.aichemistryexplorer

internal data class NavigationResult(val screen: Screen, val history: List<Screen>)

internal fun pushNavigation(current: Screen, history: List<Screen>, destination: Screen): NavigationResult =
    if (current == destination) NavigationResult(current, history)
    else NavigationResult(destination, history + current)

internal fun popNavigation(history: List<Screen>): NavigationResult =
    NavigationResult(history.lastOrNull() ?: Screen.Home, history.dropLast(1))

internal fun homeNavigation(): NavigationResult = NavigationResult(Screen.Home, emptyList())
