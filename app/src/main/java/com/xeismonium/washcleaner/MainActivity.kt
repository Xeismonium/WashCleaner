package com.xeismonium.washcleaner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.xeismonium.washcleaner.data.local.datastore.UserPreferencesRepository
import com.xeismonium.washcleaner.ui.screen.settings.ThemeMode
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import android.content.Intent
import com.xeismonium.washcleaner.util.NotificationHelper

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val pendingTransactionId = intent.getLongExtra(NotificationHelper.EXTRA_TRANSACTION_ID, -1L).takeIf { it != -1L }
        
        setContent {
            val themeMode by userPreferencesRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            WashCleanerTheme(darkTheme = darkTheme) {
                WashCleanerApp(pendingTransactionId = pendingTransactionId)
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the intent so recreation picks it up if needed, though with launchMode singleTop usually we handle here. 
        // For simplicity with Compose, typically we might rely on the Activity recreation or just ignore newIntent if we don't handle it dynamically.
        // But since we are passing it to setContent, a simple recreation (default behavior if launchMode is standard) works. 
        // If launchMode is singleTop, onNewIntent is called. We should probably trigger a recomposition or a state update.
        // Given the code structure, the simplest way is to let the default activity launch handle it (standard launch mode).
    }
}