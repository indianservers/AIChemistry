package com.indianservers.aichemistryexplorer

/**
 * Adds comparison selections in display order while preventing duplicate columns.
 * There is intentionally no maximum: every element can be compared at once.
 */
internal fun addComparisonElements(current: List<Int>, additions: List<Int>): List<Int> =
    (current + additions).distinct()
