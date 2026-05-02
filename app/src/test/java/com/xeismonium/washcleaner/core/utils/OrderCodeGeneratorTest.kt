package com.xeismonium.washcleaner.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderCodeGeneratorTest {

    @Test
    fun `generate should return correctly formatted code`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.JANUARY, 1)
        val date = calendar.time
        val count = 1L

        // When
        val result = OrderCodeGenerator.generate(date, count)

        // Then
        assertEquals("WC-20240101-001", result)
    }

    @Test
    fun `generate should pad count with zeros`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.MAY, 15)
        val date = calendar.time
        
        // When & Then
        assertEquals("WC-20240515-001", OrderCodeGenerator.generate(date, 1L))
        assertEquals("WC-20240515-010", OrderCodeGenerator.generate(date, 10L))
        assertEquals("WC-20240515-100", OrderCodeGenerator.generate(date, 100L))
        assertEquals("WC-20240515-1000", OrderCodeGenerator.generate(date, 1000L))
    }
}
