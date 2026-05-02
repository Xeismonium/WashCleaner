package com.xeismonium.washcleaner.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val localeID = Locale("id", "ID")

    fun format(date: Date, pattern: String = "dd MMM yyyy"): String {
        return SimpleDateFormat(pattern, localeID).format(date)
    }

    fun format(timestamp: Long, pattern: String = "dd MMM yyyy"): String {
        return format(Date(timestamp), pattern)
    }
}
