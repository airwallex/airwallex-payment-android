package com.airwallex.android.view

import com.airwallex.android.view.util.ExpiryDateUtils
import com.airwallex.android.view.util.ExpiryDateVisualTransformation
import com.airwallex.android.view.util.createExpiryMonthAndYear
import java.util.Calendar
import java.util.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExpiryDateUtilsTest {

    @Test
    fun `isExpiryDateValid should return true for valid future date`() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.MONTH, Calendar.JANUARY)
        }

        // Same year, future month
        assertTrue(ExpiryDateUtils.isExpiryDateValid(2, 23, calendar))

        // Future year, any month
        assertTrue(ExpiryDateUtils.isExpiryDateValid(1, 24, calendar))
    }

    @Test
    fun `isExpiryDateValid should return false for invalid month`() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.MONTH, Calendar.JANUARY)
        }

        // Month too low
        assertFalse(ExpiryDateUtils.isExpiryDateValid(0, 23, calendar))

        // Month too high
        assertFalse(ExpiryDateUtils.isExpiryDateValid(13, 23, calendar))
    }

    @Test
    fun `isExpiryDateValid should return false for invalid year`() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.MONTH, Calendar.JANUARY)
        }

        // Year too low
        assertFalse(ExpiryDateUtils.isExpiryDateValid(1, -1, calendar))

        // Year too high
        assertFalse(ExpiryDateUtils.isExpiryDateValid(1, 100, calendar))
    }

    @Test
    fun `isValidMonth should validate month correctly`() {
        // Valid months
        assertTrue(ExpiryDateUtils.isValidMonth("1"))
        assertTrue(ExpiryDateUtils.isValidMonth("12"))

        // Invalid months
        assertFalse(ExpiryDateUtils.isValidMonth(""))
        assertFalse(ExpiryDateUtils.isValidMonth("0"))
        assertFalse(ExpiryDateUtils.isValidMonth("13"))
        assertFalse(ExpiryDateUtils.isValidMonth("ab"))
        assertFalse(ExpiryDateUtils.isValidMonth(null))
    }

    @Test
    fun `isValidExpiryDate should validate full expiry date string`() {
        // Valid dates - both raw and formatted
        assertTrue(ExpiryDateUtils.isValidExpiryDate("12/30")) // Future date with slash
        assertTrue(ExpiryDateUtils.isValidExpiryDate("1230")) // Future date without slash

        // Invalid formats
        assertFalse(ExpiryDateUtils.isValidExpiryDate(""))
        assertFalse(ExpiryDateUtils.isValidExpiryDate("12"))
        assertFalse(ExpiryDateUtils.isValidExpiryDate("12/"))
        assertFalse(ExpiryDateUtils.isValidExpiryDate("12/2"))
        assertFalse(ExpiryDateUtils.isValidExpiryDate("12/250")) // Invalid year format
        assertFalse(ExpiryDateUtils.isValidExpiryDate("13/25")) // Invalid month
        assertFalse(ExpiryDateUtils.isValidExpiryDate("00/25")) // Invalid month
        assertFalse(ExpiryDateUtils.isValidExpiryDate("1325")) // Invalid month without slash
        assertFalse(ExpiryDateUtils.isValidExpiryDate("0025")) // Invalid month without slash
    }

    @Test
    fun `formatRawExpiryInput should format digits correctly`() {
        // Empty input
        assertEquals("", ExpiryDateUtils.formatRawExpiryInput(""))

        // Single digit 0 or 1 - keep as-is
        assertEquals("0", ExpiryDateUtils.formatRawExpiryInput("0"))
        assertEquals("1", ExpiryDateUtils.formatRawExpiryInput("1"))

        // Single digit 2-9 - auto-prefix with 0
        assertEquals("02", ExpiryDateUtils.formatRawExpiryInput("2"))
        assertEquals("09", ExpiryDateUtils.formatRawExpiryInput("9"))

        // Two digits - keep as-is
        assertEquals("12", ExpiryDateUtils.formatRawExpiryInput("12"))
        assertEquals("03", ExpiryDateUtils.formatRawExpiryInput("03"))

        // Full input
        assertEquals("1225", ExpiryDateUtils.formatRawExpiryInput("1225"))

        // Filters non-digit characters
        assertEquals("1225", ExpiryDateUtils.formatRawExpiryInput("12/25"))
        assertEquals("1225", ExpiryDateUtils.formatRawExpiryInput("12a25"))
    }

    @Test
    fun `ExpiryDateVisualTransformation should insert slash after position 2`() {
        val transformation = ExpiryDateVisualTransformation()

        // Empty input
        val result0 = transformation.filter(androidx.compose.ui.text.AnnotatedString(""))
        assertEquals("", result0.text.text)

        // Single digit
        val result1 = transformation.filter(androidx.compose.ui.text.AnnotatedString("1"))
        assertEquals("1", result1.text.text)

        // Two digits - no slash yet
        val result2 = transformation.filter(androidx.compose.ui.text.AnnotatedString("12"))
        assertEquals("12", result2.text.text)

        // Three digits - slash inserted
        val result3 = transformation.filter(androidx.compose.ui.text.AnnotatedString("123"))
        assertEquals("12/3", result3.text.text)

        // Four digits - full display
        val result4 = transformation.filter(androidx.compose.ui.text.AnnotatedString("1225"))
        assertEquals("12/25", result4.text.text)
    }

    @Test
    fun `ExpiryDateVisualTransformation offset mapping should be correct`() {
        val transformation = ExpiryDateVisualTransformation()
        val result = transformation.filter(androidx.compose.ui.text.AnnotatedString("1225"))
        val mapping = result.offsetMapping

        // originalToTransformed: raw position -> display position
        assertEquals(0, mapping.originalToTransformed(0)) // before '1'
        assertEquals(1, mapping.originalToTransformed(1)) // before '2'
        assertEquals(2, mapping.originalToTransformed(2)) // before '/' (position 2 in raw = position 2 in display)
        assertEquals(4, mapping.originalToTransformed(3)) // before '5' (position 3 in raw = position 4 in display, after '/')
        assertEquals(5, mapping.originalToTransformed(4)) // end

        // transformedToOriginal: display position -> raw position
        assertEquals(0, mapping.transformedToOriginal(0)) // '1'
        assertEquals(1, mapping.transformedToOriginal(1)) // '2'
        assertEquals(2, mapping.transformedToOriginal(2)) // '/' maps back to position 2
        assertEquals(2, mapping.transformedToOriginal(3)) // '2' in year (display pos 3 -> raw pos 2)
        assertEquals(3, mapping.transformedToOriginal(4)) // '5' in year (display pos 4 -> raw pos 3)
        assertEquals(4, mapping.transformedToOriginal(5)) // end
    }

    @Test
    fun `separateDateInput should split date string correctly`() {
        // Valid input
        assertArrayEquals(arrayOf("12", "25"), ExpiryDateUtils.separateDateInput("1225"))

        // Short input
        assertArrayEquals(arrayOf("1", ""), ExpiryDateUtils.separateDateInput("1"))

        // Empty input
        assertArrayEquals(arrayOf("", ""), ExpiryDateUtils.separateDateInput(""))
    }

    @Test
    fun `createExpiryMonthAndYear should parse date string correctly`() {
        // Valid input with slash
        val (month1, year1) = "12/25".createExpiryMonthAndYear() ?: throw IllegalStateException()
        assertEquals(12, month1)
        assertEquals(2025, year1)

        // Valid input without slash (raw digits from new format)
        val (month2, year2) = "1225".createExpiryMonthAndYear() ?: throw IllegalStateException()
        assertEquals(12, month2)
        assertEquals(2025, year2)

        // Partial date - the current implementation will treat this as month=12, year=20
        val (month3, year3) = "12".createExpiryMonthAndYear() ?: throw IllegalStateException()
        assertEquals(12, month3)
        assertEquals(20, year3)

        // Partial date with slash - the current implementation will treat this as month=12, year=20
        val (month4, year4) = "12/".createExpiryMonthAndYear() ?: throw IllegalStateException()
        assertEquals(12, month4)
        assertEquals(20, year4)

        // Invalid format - should return null
        assertNull("ab/cd".createExpiryMonthAndYear())

        // Note: The current implementation doesn't validate month range, so these will pass but return invalid months
        val (month5, year5) = "13/25".createExpiryMonthAndYear() ?: throw IllegalStateException()
        assertEquals(13, month5)
        assertEquals(2025, year5)

        val (month6, year6) = "00/25".createExpiryMonthAndYear() ?: throw IllegalStateException()
        assertEquals(0, month6)
        assertEquals(2025, year6)
    }

    private fun assertArrayEquals(expected: Array<String>, actual: Array<String>) {
        assertEquals(expected.size, actual.size, "Array sizes differ")
        expected.indices.forEach {
            assertEquals(expected[it], actual[it], "Element at index $it differs")
        }
    }
}
