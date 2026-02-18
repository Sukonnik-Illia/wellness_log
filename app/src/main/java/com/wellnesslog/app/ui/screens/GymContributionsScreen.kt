package com.wellnesslog.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wellnesslog.app.data.WellnessRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymContributionsScreen(
    repository: WellnessRepository,
    onNavigateBack: () -> Unit
) {
    var gymVisits by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }
    val scope = rememberCoroutineScope()
    val today = LocalDate.now()
    val startOfYear = LocalDate.of(today.year, 1, 1)

    LaunchedEffect(Unit) {
        val entries = repository.getEntriesInRange(startOfYear, today)
        gymVisits = entries.filter { it.gymVisit }.map { it.date }.toSet()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gym Contributions") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Gym visits in ${today.year}",
                style = MaterialTheme.typography.headlineSmall
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    GitHubStyleContributions(
                        gymVisits = gymVisits,
                        startDate = startOfYear,
                        endDate = today
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Statistics", style = MaterialTheme.typography.titleMedium)
                    Text("Total gym visits: ${gymVisits.size}")
                    val daysInYear = ChronoUnit.DAYS.between(startOfYear, today) + 1
                    val percentage = (gymVisits.size.toFloat() / daysInYear * 100).toInt()
                    Text("Attendance rate: $percentage%")
                }
            }
        }
    }
}

@Composable
fun GitHubStyleContributions(
    gymVisits: Set<LocalDate>,
    startDate: LocalDate,
    endDate: LocalDate
) {
    val cellSize = 12.dp
    val cellSpacing = 3.dp
    val totalCellSize = cellSize + cellSpacing

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalCellSize * 7)
    ) {
        val cellSizePx = cellSize.toPx()
        val cellSpacingPx = cellSpacing.toPx()
        val totalCellSizePx = cellSizePx + cellSpacingPx

        var currentDate = startDate
        var col = 0
        
        while (!currentDate.isAfter(endDate)) {
            val row = currentDate.dayOfWeek.value % 7
            
            val color = if (gymVisits.contains(currentDate)) {
                Color(0xFF4CAF50) // Green for gym visit
            } else {
                Color(0xFFEEEEEE) // Light gray for no visit
            }

            drawRect(
                color = color,
                topLeft = Offset(
                    x = col * totalCellSizePx,
                    y = row * totalCellSizePx
                ),
                size = Size(cellSizePx, cellSizePx)
            )

            if (row == 6) {
                col++
            }
            
            currentDate = currentDate.plusDays(1)
        }
    }
}
