package com.xeismonium.washcleaner.ui.components.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

/**
 * Consistent section header for dashboard sections
 *
 * @param title Section title text
 * @param actionText Optional action button text (e.g., "Lihat Semua")
 * @param onActionClick Optional click handler for action button
 * @param modifier Modifier for customization
 */
@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

@Preview(name = "With Action")
@Composable
private fun SectionHeaderPreviewWithAction() {
    WashCleanerTheme {
        SectionHeader(
            title = "Transaksi Terbaru",
            actionText = "Lihat Semua",
            onActionClick = {}
        )
    }
}

@Preview(name = "Without Action")
@Composable
private fun SectionHeaderPreviewWithoutAction() {
    WashCleanerTheme {
        SectionHeader(
            title = "Statistik Cepat"
        )
    }
}

@Preview(name = "Dark Mode")
@Composable
private fun SectionHeaderPreviewDark() {
    WashCleanerTheme(darkTheme = true) {
        SectionHeader(
            title = "Status Pembayaran",
            actionText = "Detail",
            onActionClick = {}
        )
    }
}
