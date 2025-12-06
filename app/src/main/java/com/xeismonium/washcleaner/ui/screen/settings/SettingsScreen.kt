package com.xeismonium.washcleaner.ui.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.events.collectAsState()

    var showRestoreDialog by remember { mutableStateOf(false) }

    LaunchedEffect(events) {
        when (events) {
            is SettingsEvent.BackupCompleted,
            is SettingsEvent.RestoreCompleted,
            is SettingsEvent.Error -> viewModel.clearEvent()
            null -> {}
        }
    }

    SettingsContent(
        uiState = uiState,
        onThemeModeChange = viewModel::setThemeMode,
        onBackupJson = viewModel::backupDataAsJson,
        onBackupDatabase = viewModel::backupDataAsDatabase,
        onRestoreJson = { showRestoreDialog = true },
        onRestoreDatabase = { showRestoreDialog = true },
        onClearMessage = viewModel::clearMessage
    )

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Pilih File Restore") },
            text = { Text("Fitur file picker akan diimplementasikan menggunakan Activity Result API") },
            confirmButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onBackupJson: () -> Unit = {},
    onBackupDatabase: () -> Unit = {},
    onRestoreJson: () -> Unit = {},
    onRestoreDatabase: () -> Unit = {},
    onClearMessage: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Pengaturan",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages
            item {
                AnimatedVisibility(
                    visible = uiState.successMessage != null || uiState.error != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        uiState.successMessage?.let {
                            MessageBanner(message = it, isError = false, onDismiss = onClearMessage)
                        }
                        uiState.error?.let {
                            MessageBanner(message = it, isError = true, onDismiss = onClearMessage)
                        }
                    }
                }
            }

            // Theme Section
            item { SectionHeader("Tampilan") }

            item {
                ThemeItem(
                    label = "Terang",
                    icon = Icons.Default.LightMode,
                    selected = uiState.themeMode == ThemeMode.LIGHT,
                    onClick = { onThemeModeChange(ThemeMode.LIGHT) }
                )
            }
            item {
                ThemeItem(
                    label = "Gelap",
                    icon = Icons.Default.DarkMode,
                    selected = uiState.themeMode == ThemeMode.DARK,
                    onClick = { onThemeModeChange(ThemeMode.DARK) }
                )
            }
            item {
                ThemeItem(
                    label = "Ikuti Sistem",
                    icon = Icons.Default.Settings,
                    selected = uiState.themeMode == ThemeMode.SYSTEM,
                    onClick = { onThemeModeChange(ThemeMode.SYSTEM) }
                )
            }

            item { SectionDivider() }

            // Backup Section
            item { SectionHeader("Cadangan") }

            item {
                SettingsItem(
                    icon = Icons.Default.FileDownload,
                    title = "Backup JSON",
                    subtitle = "Simpan data sebagai file JSON",
                    enabled = !uiState.isBackingUp,
                    onClick = onBackupJson
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Storage,
                    title = "Backup Database",
                    subtitle = "Simpan data sebagai file database",
                    enabled = !uiState.isBackingUp,
                    onClick = onBackupDatabase
                )
            }

            if (uiState.isBackingUp) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            item { SectionDivider() }

            // Restore Section
            item { SectionHeader("Pulihkan") }

            item {
                SettingsItem(
                    icon = Icons.Default.FileUpload,
                    title = "Restore JSON",
                    subtitle = "Pulihkan dari file JSON",
                    enabled = !uiState.isRestoring,
                    onClick = onRestoreJson
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Restore,
                    title = "Restore Database",
                    subtitle = "Pulihkan dari file database",
                    enabled = !uiState.isRestoring,
                    onClick = onRestoreDatabase
                )
            }

            if (uiState.isRestoring) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            item { SectionDivider() }

            // About Section
            item { SectionHeader("Tentang") }

            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Versi",
                    subtitle = uiState.appVersion,
                    onClick = {}
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(top = 8.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled)
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (enabled)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun ThemeItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

@Composable
private fun MessageBanner(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isError) Icons.Default.Error else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError)
                    MaterialTheme.colorScheme.onErrorContainer
                else
                    MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = if (isError)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    WashCleanerTheme {
        SettingsContent(
            uiState = SettingsUiState(
                appVersion = "1.0.0",
                themeMode = ThemeMode.SYSTEM
            )
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsPreviewDark() {
    WashCleanerTheme {
        SettingsContent(
            uiState = SettingsUiState(
                appVersion = "1.0.0",
                themeMode = ThemeMode.DARK
            )
        )
    }
}
