package com.xeismonium.washcleaner.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xeismonium.washcleaner.domain.model.UserRole
import com.xeismonium.washcleaner.ui.auth.LoginScreen
import com.xeismonium.washcleaner.ui.auth.RegisterScreen
import com.xeismonium.washcleaner.ui.auth.SplashScreen
import com.xeismonium.washcleaner.ui.customer.AddEditCustomerScreen
import com.xeismonium.washcleaner.ui.customer.CustomerDetailScreen
import com.xeismonium.washcleaner.ui.customer.CustomerListScreen
import com.xeismonium.washcleaner.ui.order.CreateOrderScreen
import com.xeismonium.washcleaner.ui.order.OrderDetailScreen
import com.xeismonium.washcleaner.ui.order.OrderListScreen
import com.xeismonium.washcleaner.ui.payment.PaymentScreen
import com.xeismonium.washcleaner.ui.report.ReportScreen
import com.xeismonium.washcleaner.ui.settings.SettingsScreen
import com.xeismonium.washcleaner.ui.settings.StaffManagementScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    userRole: UserRole? = null,
    startDestination: String = Route.Splash.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Route.Splash.route) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Route.Orders.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.Login.route) {
            LoginScreen(
                onNavigateToMain = {
                    navController.navigate(Route.Orders.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMain = {
                    navController.navigate(Route.Orders.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.ForgotPassword.route) {
            PlaceholderScreen("Forgot Password Screen")
        }
        composable(Route.Main.route) {
            // Main route usually redirects to Orders or is the wrapper itself.
            // Since MainScreen already hosts NavGraph, this might be redundant.
            // For now, redirect to Orders.
            navController.navigate(Route.Orders.route) {
                popUpTo(Route.Main.route) { inclusive = true }
            }
        }
        composable(Route.Orders.route) {
            OrderListScreen(
                onAddOrder = { navController.navigate(Route.AddOrder.route) },
                onOrderClick = { orderId -> navController.navigate(Route.OrderDetail.createRoute(orderId)) }
            )
        }
        composable(Route.Customers.route) {
            CustomerListScreen(
                onAddCustomer = { navController.navigate(Route.AddCustomer.route) },
                onCustomerClick = { customerId -> navController.navigate(Route.CustomerDetail.createRoute(customerId)) }
            )
        }
        
        // Reports only available for Admin/Owner
        if (userRole == UserRole.OWNER) {
            composable(Route.Reports.route) {
                ReportScreen(
                    onOrderClick = { orderId -> navController.navigate(Route.OrderDetail.createRoute(orderId)) }
                )
            }
        }
        
        composable(Route.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToStaff = { navController.navigate(Route.ManageUsers.route) },
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Order Sub-routes
        composable(Route.AddOrder.route) {
            CreateOrderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Route.OrderDetail.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Route.EditOrder.route) { backStackEntry ->
            CreateOrderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Customer Sub-routes
        composable(Route.AddCustomer.route) {
            AddEditCustomerScreen(
                customerId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Route.CustomerDetail.route) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            CustomerDetailScreen(
                customerId = customerId,
                onNavigateBack = { navController.popBackStack() },
                onEditCustomer = { id -> navController.navigate(Route.EditCustomer.createRoute(id)) },
                onOrderClick = { orderId -> navController.navigate(Route.OrderDetail.createRoute(orderId)) }
            )
        }
        composable(Route.EditCustomer.route) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")
            AddEditCustomerScreen(
                customerId = customerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.Payment.route) {
            PaymentScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Settings Sub-routes
        composable(Route.StoreSettings.route) {
            PlaceholderScreen("Store Settings")
        }
        composable(Route.ServiceManagement.route) {
            PlaceholderScreen("Service Management")
        }
        composable(Route.ManageUsers.route) {
            StaffManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
