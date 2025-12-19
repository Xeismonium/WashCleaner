package com.xeismonium.washcleaner

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.xeismonium.washcleaner.ui.navigation.NavGraph
import com.xeismonium.washcleaner.ui.navigation.Screen
import com.xeismonium.washcleaner.ui.screen.settings.SettingsViewModel

@Composable
fun WashCleanerApp(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val uiState by settingsViewModel.uiState.collectAsState()

    // Restore last screen route
    LaunchedEffect(uiState.lastScreenRoute) {
        val lastRoute = uiState.lastScreenRoute
        if (lastRoute != null && lastRoute != Screen.Dashboard.route) {
            // Check if we are already there to avoid loop or unnecessary navigation
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute != lastRoute) {
                 // We want to push it on top of Dashboard so Back works
                 navController.navigate(lastRoute) {
                     launchSingleTop = true
                 }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
        // BottomBar removed
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
