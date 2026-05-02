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
            PlaceholderScreen("Splash Screen")
        }
        composable(Route.Login.route) {
            PlaceholderScreen("Login Screen")
        }
        composable(Route.Main.route) {
            PlaceholderScreen("Main Screen")
        }
        composable(Route.Orders.route) {
            PlaceholderScreen("Orders Screen")
        }
        composable(Route.Customers.route) {
            PlaceholderScreen("Customers Screen")
        }
        
        // Reports only available for Admin/Owner
        if (userRole == UserRole.OWNER) { // Assuming OWNER is the top role, or if you have ADMIN add it
            composable(Route.Reports.route) {
                PlaceholderScreen("Reports Screen")
            }
        }
        
        composable(Route.Settings.route) {
            PlaceholderScreen("Settings Screen")
        }
        
        // Order Sub-routes
        composable(Route.AddOrder.route) {
            PlaceholderScreen("Add Order")
        }
        composable(Route.OrderDetail.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            PlaceholderScreen("Order Detail: $orderId")
        }
        composable(Route.EditOrder.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            PlaceholderScreen("Edit Order: $orderId")
        }
        
        // Customer Sub-routes
        composable(Route.AddCustomer.route) {
            PlaceholderScreen("Add Customer")
        }
        composable(Route.CustomerDetail.route) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")
            PlaceholderScreen("Customer Detail: $customerId")
        }
        composable(Route.EditCustomer.route) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")
            PlaceholderScreen("Edit Customer: $customerId")
        }
        
        // Settings Sub-routes
        composable(Route.StoreSettings.route) {
            PlaceholderScreen("Store Settings")
        }
        composable(Route.ServiceManagement.route) {
            PlaceholderScreen("Service Management")
        }
        composable(Route.ManageUsers.route) {
            PlaceholderScreen("Manage Users")
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
