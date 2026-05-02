package com.xeismonium.washcleaner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.xeismonium.washcleaner.domain.model.UserRole
import com.xeismonium.washcleaner.ui.main.MainScreen
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import dagger.hilt.android.AndroidEntryPoint

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WashCleanerTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()
                
                // TODO: Collect this from an AuthViewModel or similar role state flow
                val userRole = UserRole.OWNER 

                CompositionLocalProvider(
                    LocalSnackbarHostState provides snackbarHostState
                ) {
                    MainScreen(
                        userRole = userRole,
                        navController = navController
                    )
                }
            }
        }
    }
}