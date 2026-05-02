package com.xeismonium.washcleaner.core.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val localeID = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getCurrencyInstance(localeID).apply {
        maximumFractionDigits = 0
    }

    fun format(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun format(amount: Long): String {
        return currencyFormat.format(amount)
    }
}
