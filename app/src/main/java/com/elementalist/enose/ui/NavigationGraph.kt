package com.elementalist.enose.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.elementalist.enose.ui.screens.ConnectionScreen
import com.elementalist.enose.ui.screens.MainScreen
import com.elementalist.enose.ui.screens.MainViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "main_screen"
    ) {
        composable("main_screen") {
            MainScreen(navController = navController, viewModel = viewModel)
        }
        composable("connection_screen") {
            ConnectionScreen(viewModel = viewModel)
        }
    }
}