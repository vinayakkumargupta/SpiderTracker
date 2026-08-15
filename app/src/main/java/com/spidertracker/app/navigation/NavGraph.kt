package com.spidertracker.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spidertracker.app.domain.model.LocationData
import com.spidertracker.app.presentation.home.HomeScreen
import com.spidertracker.app.presentation.tracker.TrackerScreen
import com.spidertracker.app.presentation.tracker.TrackerViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartTracking = {
                    // In a real app, request permissions here
                    // For now, we'll just go to Tracker with a default location
                    navController.navigate(Screen.Tracker.route)
                },
                onDemoMode = {
                    navController.navigate(Screen.Tracker.route + "?demo=true")
                }
            )
        }
        composable(
            route = Screen.Tracker.route + "?demo={demo}",
            arguments = listOf(
                androidx.navigation.navArgument("demo") {
                    defaultValue = "false"
                }
            )
        ) { backStackEntry ->
            val isDemo = backStackEntry.arguments?.getString("demo") == "true"
            val viewModel: TrackerViewModel = viewModel()
            
            // Default location: Bengaluru (MG Road)
            val defaultLocation = LocationData(12.9716, 77.5946)
            
            LaunchedEffect(Unit) {
                viewModel.startTracking(defaultLocation, isDemo)
            }

            TrackerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

