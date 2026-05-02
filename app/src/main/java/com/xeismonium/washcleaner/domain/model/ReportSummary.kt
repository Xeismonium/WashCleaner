package com.xeismonium.washcleaner.domain.model

data class ReportSummary(
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val completedOrders: Int = 0,
    val pendingOrders: Int = 0
)
