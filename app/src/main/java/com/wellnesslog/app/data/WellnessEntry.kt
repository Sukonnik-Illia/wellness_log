package com.wellnesslog.app.data

import java.time.LocalDate

data class WellnessEntry(
    val date: LocalDate,
    val gymVisit: Boolean = false,
    val calorieDeficit: Boolean = false,
    val weight: Float? = null
)
