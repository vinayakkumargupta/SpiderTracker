package com.spidertracker.app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Tracker : Screen("tracker")
}
