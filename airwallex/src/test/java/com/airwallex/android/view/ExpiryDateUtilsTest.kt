package com.airwallex.android.view

import java.util.Calendar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExpiryDateUtilsTest {

    @Test
    fun convertTwoDigitYearToFourTest() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2066)
        assertEquals(ExpiryDateUtils.convertTwoDigitYearToFour(80, calendar), 2080)
    }

    @Test
    fun isExpiryDataValidTest() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2018)
        calendar.set(Calendar.MONTH, Calendar.JANUARY)

        assertTrue(ExpiryDateUtils.isExpiryDateValid(1, 2019, calendar))
    }

    @Test
    fun isValidMonthTest() {
        assertFalse(ExpiryDateUtils.isValidMonth("20"))
        assertFalse(ExpiryDateUtils.isValidMonth("0"))
    }
}
