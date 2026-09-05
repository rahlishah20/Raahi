package com.example.navigation

sealed class RaahiDestination(val route: String) {
    data object Home : RaahiDestination("home")
    data object Search : RaahiDestination("search")
    data object History : RaahiDestination("history")
    data object Profile : RaahiDestination("profile")
    data object EnvironmentControl : RaahiDestination("environment_control")
}
