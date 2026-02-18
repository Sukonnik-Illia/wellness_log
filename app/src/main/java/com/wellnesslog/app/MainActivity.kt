package com.wellnesslog.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wellnesslog.app.data.WellnessRepository
import com.wellnesslog.app.ui.screens.GymContributionsScreen
import com.wellnesslog.app.ui.screens.MainScreen
import com.wellnesslog.app.ui.screens.WeightGraphScreen
import com.wellnesslog.app.ui.theme.WellnessLogTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: WellnessRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = WellnessRepository(this)

        setContent {
            WellnessLogTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WellnessLogApp(repository)
                }
            }
        }
    }
}

@Composable
fun WellnessLogApp(repository: WellnessRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                repository = repository,
                onNavigateToWeightGraph = { navController.navigate("weight_graph") },
                onNavigateToGymContributions = { navController.navigate("gym_contributions") }
            )
        }
        composable("weight_graph") {
            WeightGraphScreen(
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("gym_contributions") {
            GymContributionsScreen(
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
