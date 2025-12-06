package com.xeismonium.washcleaner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.xeismonium.washcleaner.ui.screen.customer.CustomerFormScreen
import com.xeismonium.washcleaner.ui.screen.customer.CustomerListScreen
import com.xeismonium.washcleaner.ui.screen.dashboard.DashboardScreen
import com.xeismonium.washcleaner.ui.screen.report.ReportScreen
import com.xeismonium.washcleaner.ui.screen.service.ServiceFormScreen
import com.xeismonium.washcleaner.ui.screen.service.ServiceListScreen
import com.xeismonium.washcleaner.ui.screen.settings.SettingsScreen
import com.xeismonium.washcleaner.ui.screen.splash.SplashScreen
import com.xeismonium.washcleaner.ui.screen.transaction.TransactionDetailScreen
import com.xeismonium.washcleaner.ui.screen.transaction.TransactionFormScreen
import com.xeismonium.washcleaner.ui.screen.transaction.TransactionListScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // 2. Dashboard / Home Screen
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        // 3. Daftar Transaksi Screen
        composable(Screen.TransactionList.route) {
            TransactionListScreen(navController = navController)
        }

        // 4. Tambah / Edit Transaksi Screen
        composable(
            route = Screen.TransactionForm.route,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            val transactionId = it.arguments?.getLong("transactionId") ?: 0L
            TransactionFormScreen(
                navController = navController,
                transactionId = transactionId
            )
        }

        // 5. Detail Transaksi Screen
        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.LongType
                }
            )
        ) {
            val transactionId = it.arguments?.getLong("transactionId") ?: 0L
            TransactionDetailScreen(
                navController = navController,
                transactionId = transactionId
            )
        }

        // 6. Daftar Layanan Screen
        composable(Screen.ServiceList.route) {
            ServiceListScreen(navController = navController)
        }

        // 7. Tambah / Edit Layanan Screen
        composable(
            route = Screen.ServiceForm.route,
            arguments = listOf(
                navArgument("serviceId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            val serviceId = it.arguments?.getLong("serviceId") ?: 0L
            ServiceFormScreen(
                navController = navController,
                serviceId = serviceId
            )
        }

        // 8. Daftar Pelanggan Screen
        composable(Screen.CustomerList.route) {
            CustomerListScreen(navController = navController)
        }

        // 9. Tambah / Edit Pelanggan Screen
        composable(
            route = Screen.CustomerForm.route,
            arguments = listOf(
                navArgument("customerId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            val customerId = it.arguments?.getLong("customerId") ?: 0L
            CustomerFormScreen(
                navController = navController,
                customerId = customerId
            )
        }

        // 10. Laporan Screen
        composable(Screen.Report.route) {
            ReportScreen(navController = navController)
        }

        // 11. Pengaturan Screen
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
