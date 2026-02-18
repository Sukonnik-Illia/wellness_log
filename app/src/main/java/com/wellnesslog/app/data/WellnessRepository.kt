package com.wellnesslog.app.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WellnessRepository(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("wellness_data", Context.MODE_PRIVATE)
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    suspend fun saveEntry(entry: WellnessEntry) = withContext(Dispatchers.IO) {
        val dateKey = entry.date.format(dateFormatter)
        prefs.edit().apply {
            putBoolean("${dateKey}_gym", entry.gymVisit)
            putBoolean("${dateKey}_calorie", entry.calorieDeficit)
            entry.weight?.let { putFloat("${dateKey}_weight", it) }
                ?: remove("${dateKey}_weight")
            apply()
        }
    }

    suspend fun getEntry(date: LocalDate): WellnessEntry? = withContext(Dispatchers.IO) {
        val dateKey = date.format(dateFormatter)
        if (!prefs.contains("${dateKey}_gym")) {
            return@withContext null
        }
        WellnessEntry(
            date = date,
            gymVisit = prefs.getBoolean("${dateKey}_gym", false),
            calorieDeficit = prefs.getBoolean("${dateKey}_calorie", false),
            weight = if (prefs.contains("${dateKey}_weight")) 
                prefs.getFloat("${dateKey}_weight", 0f) else null
        )
    }

    suspend fun getEntriesInRange(startDate: LocalDate, endDate: LocalDate): List<WellnessEntry> =
        withContext(Dispatchers.IO) {
            val entries = mutableListOf<WellnessEntry>()
            var currentDate = startDate
            while (!currentDate.isAfter(endDate)) {
                getEntry(currentDate)?.let { entries.add(it) }
                currentDate = currentDate.plusDays(1)
            }
            entries
        }

    suspend fun getLastWeight(beforeDate: LocalDate): Float? = withContext(Dispatchers.IO) {
        var currentDate = beforeDate.minusDays(1)
        repeat(365) {
            val entry = getEntry(currentDate)
            if (entry?.weight != null) {
                return@withContext entry.weight
            }
            currentDate = currentDate.minusDays(1)
        }
        null
    }

    suspend fun getAllEntries(): List<WellnessEntry> = withContext(Dispatchers.IO) {
        val allEntries = mutableListOf<WellnessEntry>()
        val allKeys = prefs.all.keys
        val dateKeys = allKeys.filter { it.endsWith("_gym") }
            .map { it.substringBefore("_gym") }
            .distinct()
        
        dateKeys.forEach { dateKey ->
            try {
                val date = LocalDate.parse(dateKey, dateFormatter)
                getEntry(date)?.let { allEntries.add(it) }
            } catch (e: Exception) {
                // Skip invalid date keys
            }
        }
        allEntries.sortedBy { it.date }
    }
}
