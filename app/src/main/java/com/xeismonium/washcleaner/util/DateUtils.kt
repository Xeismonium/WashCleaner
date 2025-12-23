package com.xeismonium.washcleaner.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    /**
     * Returns a human-readable relative time string in Indonesian
     * Examples: "Baru saja", "5 menit lalu", "2 jam lalu", "Kemarin", "2 hari lalu"
     */
    fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Baru saja"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes menit lalu"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours jam lalu"
            }
            diff < TimeUnit.DAYS.toMillis(2) -> "Kemarin"
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "$days hari lalu"
            }
            else -> {
                // For older dates, show the actual date
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
                dateFormat.format(Date(timestamp))
            }
        }
    }

    /**
     * Formats a timestamp into Indonesian date format
     * Example: "Rabu, 23 Desember 2025"
     */
    fun formatDateIndonesian(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.forLanguageTag("id-ID"))
        return dateFormat.format(Date(timestamp))
    }

    /**
     * Gets a time-based greeting in Indonesian
     * Returns: "Selamat Pagi", "Selamat Siang", "Selamat Sore", or "Selamat Malam"
     */
    fun getGreeting(timestamp: Long = System.currentTimeMillis()): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 0..10 -> "Selamat Pagi"
            in 11..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }
}
