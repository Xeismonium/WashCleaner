package com.xeismonium.washcleaner.ui.navigation

sealed class Screen(val route: String) {
    // 1. Splash Screen
    data object Splash : Screen("splash")

    // 2. Dashboard / Home Screen
    data object Dashboard : Screen("dashboard")

    // 3. Daftar Transaksi Screen
    data object TransactionList : Screen("transaction_list")

    // 4. Tambah / Edit Transaksi Screen
    data object TransactionForm : Screen("transaction_form/{transactionId}") {
        fun createRoute(transactionId: Long? = null) =
            if (transactionId != null) "transaction_form/$transactionId" else "transaction_form/0"
    }

    // 5. Detail Transaksi Screen
    data object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: Long) = "transaction_detail/$transactionId"
    }

    // 6. Daftar Layanan Screen
    data object ServiceList : Screen("service_list")

    // 7. Tambah / Edit Layanan Screen
    data object ServiceForm : Screen("service_form/{serviceId}") {
        fun createRoute(serviceId: Long? = null) =
            if (serviceId != null) "service_form/$serviceId" else "service_form/0"
    }

    // 8. Daftar Pelanggan Screen
    data object CustomerList : Screen("customer_list")

    // 9. Tambah / Edit Pelanggan Screen
    data object CustomerForm : Screen("customer_form/{customerId}") {
        fun createRoute(customerId: Long? = null) =
            if (customerId != null) "customer_form/$customerId" else "customer_form/0"
    }

    // 10. Laporan Screen
    data object Report : Screen("report")

    // 11. Pengaturan Screen
    data object Settings : Screen("settings")
}
