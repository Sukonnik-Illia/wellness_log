package com.wellnesslog.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.wellnesslog.app.data.WellnessRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightGraphScreen(
    repository: WellnessRepository,
    onNavigateBack: () -> Unit
) {
    var startDate by remember { mutableStateOf(LocalDate.now().minusMonths(1)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var entries by remember { mutableStateOf<List<Entry>>(emptyList()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(startDate, endDate) {
        val wellnessEntries = repository.getEntriesInRange(startDate, endDate)
        val weightEntries = wellnessEntries
            .filter { it.weight != null }
            .mapIndexed { index, entry -> 
                Entry(index.toFloat(), entry.weight!!) 
            }
        entries = weightEntries
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weight Graph") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    onClick = { showStartDatePicker = true }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Start Date", style = MaterialTheme.typography.labelSmall)
                        Text(
                            startDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    onClick = { showEndDatePicker = true }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("End Date", style = MaterialTheme.typography.labelSmall)
                        Text(
                            endDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                if (entries.isNotEmpty()) {
                    AndroidView(
                        factory = { context ->
                            LineChart(context).apply {
                                description.isEnabled = false
                                setTouchEnabled(true)
                                setPinchZoom(true)
                            }
                        },
                        update = { chart ->
                            val dataSet = LineDataSet(entries, "Weight (kg)").apply {
                                color = android.graphics.Color.parseColor("#4CAF50")
                                lineWidth = 2f
                                setCircleColor(android.graphics.Color.parseColor("#4CAF50"))
                                circleRadius = 4f
                                setDrawValues(false)
                            }
                            chart.data = LineData(dataSet)
                            chart.invalidate()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text("No weight data in selected range")
                    }
                }
            }
        }
    }

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = startDate.toEpochDay() * 86400000
            )
            DatePicker(state = datePickerState)
            
            LaunchedEffect(datePickerState.selectedDateMillis) {
                datePickerState.selectedDateMillis?.let {
                    val days = it / 86400000
                    startDate = LocalDate.ofEpochDay(days)
                }
            }
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = endDate.toEpochDay() * 86400000
            )
            DatePicker(state = datePickerState)
            
            LaunchedEffect(datePickerState.selectedDateMillis) {
                datePickerState.selectedDateMillis?.let {
                    val days = it / 86400000
                    endDate = LocalDate.ofEpochDay(days)
                }
            }
        }
    }
}
