package com.xeismonium.washcleaner.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Dashboard : NavigationItem(
        route = Screen.Dashboard.route,
        title = "Dashboard",
        icon = Icons.Default.Dashboard
    )

    data object Transaction : NavigationItem(
        route = Screen.TransactionList.route,
        title = "Transaksi",
        icon = Icons.Default.Receipt
    )

    data object Service : NavigationItem(
        route = Screen.ServiceList.route,
        title = "Layanan",
        icon = Icons.Default.LocalLaundryService
    )

    data object Customer : NavigationItem(
        route = Screen.CustomerList.route,
        title = "Pelanggan",
        icon = Icons.Default.AccountCircle
    )

    data object Report : NavigationItem(
        route = Screen.Report.route,
        title = "Laporan",
        icon = Icons.Default.Assessment
    )

    data object Settings : NavigationItem(
        route = Screen.Settings.route,
        title = "Pengaturan",
        icon = Icons.Default.Settings
    )

    companion object {
        // Bottom Navigation Items (main features only)
        val bottomNavItems = listOf(
            Dashboard,
            Transaction,
            Service,
            Customer
        )

        // All Navigation Items (including Report and Settings for drawer)
        val allItems = listOf(
            Dashboard,
            Transaction,
            Service,
            Customer,
            Report,
            Settings
        )
    }
}
