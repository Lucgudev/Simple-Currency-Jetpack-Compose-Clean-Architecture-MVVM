package com.lucgu.findmycurrencies.presentation.feature.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lucgu.findmycurrencies.presentation.feature.home.HomeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.HOME_ROUTE) {
        composable(Route.HOME_ROUTE) {
            HomeScreen()
        }
    }
}