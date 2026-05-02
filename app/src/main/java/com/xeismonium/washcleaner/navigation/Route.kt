package com.xeismonium.washcleaner.navigation

sealed class Route(val route: String) {
    // Auth & Onboarding
    object Splash : Route("splash")
    object Login : Route("login")
    
    // Main Tabs
    object Main : Route("main")
    object Orders : Route("orders")
    object Customers : Route("customers")
    object Reports : Route("reports")
    object Settings : Route("settings")
    
    // Order Sub-routes
    object AddOrder : Route("add_order")
    object OrderDetail : Route("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
    object EditOrder : Route("edit_order/{orderId}") {
        fun createRoute(orderId: String) = "edit_order/$orderId"
    }
    
    // Customer Sub-routes
    object AddCustomer : Route("add_customer")
    object CustomerDetail : Route("customer_detail/{customerId}") {
        fun createRoute(customerId: String) = "customer_detail/$customerId"
    }
    object EditCustomer : Route("edit_customer/{customerId}") {
        fun createRoute(customerId: String) = "edit_customer/$customerId"
    }

    object Payment : Route("payment/{orderId}") {
        fun createRoute(orderId: String) = "payment/$orderId"
    }
    
    // Settings Sub-routes
    object StoreSettings : Route("store_settings")
    object ServiceManagement : Route("service_management")
    object ManageUsers : Route("manage_users")
}
