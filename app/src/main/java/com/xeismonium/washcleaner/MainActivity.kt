package com.xeismonium.washcleaner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.xeismonium.washcleaner.data.session.SessionManager
import com.xeismonium.washcleaner.domain.model.UserRole
import com.xeismonium.washcleaner.ui.main.MainScreen
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WashCleanerTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()
                
                val session by sessionManager.sessionFlow.collectAsStateWithLifecycle(initialValue = null)
                val userRole = session?.role ?: UserRole.STAFF

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