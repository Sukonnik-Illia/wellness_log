package com.wellnesslog.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wellnesslog.app.data.WellnessEntry
import com.wellnesslog.app.data.WellnessRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repository: WellnessRepository,
    onNavigateToWeightGraph: () -> Unit,
    onNavigateToGymContributions: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var gymVisit by remember { mutableStateOf(false) }
    var calorieDeficit by remember { mutableStateOf(false) }
    var weight by remember { mutableStateOf<Float?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedDate) {
        val entry = repository.getEntry(selectedDate)
        if (entry != null) {
            gymVisit = entry.gymVisit
            calorieDeficit = entry.calorieDeficit
            weight = entry.weight
        } else {
            gymVisit = false
            calorieDeficit = false
            weight = repository.getLastWeight(selectedDate)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wellness Log") },
                actions = {
                    TextButton(onClick = onNavigateToWeightGraph) {
                        Text("Weight Graph")
                    }
                    TextButton(onClick = onNavigateToGymContributions) {
                        Text("Gym Stats")
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDatePicker = true }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Date", style = MaterialTheme.typography.labelMedium)
                    Text(
                        selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Gym Visit", style = MaterialTheme.typography.bodyLarge)
                        Checkbox(
                            checked = gymVisit,
                            onCheckedChange = { gymVisit = it }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Calorie Deficit", style = MaterialTheme.typography.bodyLarge)
                        Checkbox(
                            checked = calorieDeficit,
                            onCheckedChange = { calorieDeficit = it }
                        )
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Weight (kg)", style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalIconButton(
                            onClick = { weight = weight?.minus(0.1f)?.coerceAtLeast(0f) },
                            shape = CircleShape
                        ) {
                            Text("-", style = MaterialTheme.typography.headlineMedium)
                        }
                        
                        Text(
                            text = weight?.let { String.format("%.1f", it) } ?: "-",
                            style = MaterialTheme.typography.displaySmall
                        )
                        
                        FilledTonalIconButton(
                            onClick = { weight = (weight ?: 0f) + 0.1f },
                            shape = CircleShape
                        ) {
                            Text("+", style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        repository.saveEntry(
                            WellnessEntry(
                                date = selectedDate,
                                gymVisit = gymVisit,
                                calorieDeficit = calorieDeficit,
                                weight = weight
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Save")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.toEpochDay() * 86400000,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val days = utcTimeMillis / 86400000
                        val date = LocalDate.ofEpochDay(days)
                        return !date.isAfter(LocalDate.now())
                    }
                }
            )
            DatePicker(state = datePickerState)
            
            LaunchedEffect(datePickerState.selectedDateMillis) {
                datePickerState.selectedDateMillis?.let {
                    val days = it / 86400000
                    selectedDate = LocalDate.ofEpochDay(days)
                }
            }
        }
    }
}
