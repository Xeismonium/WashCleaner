package com.xeismonium.washcleaner

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
fun WashCleanerApp() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavGraph(
            navController = navController
        )
    }
}
