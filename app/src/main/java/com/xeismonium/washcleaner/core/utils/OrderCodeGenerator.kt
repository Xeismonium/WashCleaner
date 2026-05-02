package com.xeismonium.washcleaner.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OrderCodeGenerator {
    /**
     * Generates an order code in the format: WC-YYYYMMDD-XXX
     * where XXX is a padded serial number.
     */
    fun generate(date: Date, count: Long): String {
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(date)
        val paddedCount = count.toString().padStart(3, '0')
        return "WC-$dateStr-$paddedCount"
    }
}
