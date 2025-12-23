package com.xeismonium.washcleaner.ui.components.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import com.xeismonium.washcleaner.util.DateUtils

/**
 * Dashboard header with personalized greeting and current date
 *
 * Features:
 * - Time-based greeting (Pagi: 0-10h, Siang: 11-14h, Sore: 15-17h, Malam: 18-24h)
 * - Indonesian date format
 *
 * @param currentTime Current timestamp in milliseconds (defaults to now)
 * @param modifier Modifier for customization
 */
@Composable
fun DashboardHeader(
    currentTime: Long = System.currentTimeMillis(),
    modifier: Modifier = Modifier
) {
    val greeting = DateUtils.getGreeting(currentTime)
    val formattedDate = DateUtils.formatDateIndonesian(currentTime)

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(name = "Light Mode - Morning")
@Composable
private fun DashboardHeaderPreviewMorning() {
    WashCleanerTheme {
        // Morning: 8 AM
        val morningTime = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 8)
            set(java.util.Calendar.MINUTE, 30)
        }.timeInMillis

        DashboardHeader(currentTime = morningTime)
    }
}

@Preview(name = "Light Mode - Afternoon")
@Composable
private fun DashboardHeaderPreviewAfternoon() {
    WashCleanerTheme {
        // Afternoon: 2 PM
        val afternoonTime = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 14)
            set(java.util.Calendar.MINUTE, 0)
        }.timeInMillis

        DashboardHeader(currentTime = afternoonTime)
    }
}

@Preview(name = "Dark Mode - Evening")
@Composable
private fun DashboardHeaderPreviewEvening() {
    WashCleanerTheme(darkTheme = true) {
        // Evening: 6 PM
        val eveningTime = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 18)
            set(java.util.Calendar.MINUTE, 45)
        }.timeInMillis

        DashboardHeader(currentTime = eveningTime)
    }
}
