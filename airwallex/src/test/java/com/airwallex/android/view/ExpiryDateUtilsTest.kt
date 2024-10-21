package com.airwallex.android.view

import com.airwallex.android.view.util.ExpiryDateUtils
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpiryDateUtilsTest {

    @Test
    fun isExpiryDataValidTest() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(Calendar.YEAR, 2018)
        calendar.set(Calendar.MONTH, Calendar.JANUARY)

        assertTrue(ExpiryDateUtils.isExpiryDateValid(1, 19, calendar))
    }

    @Test
    fun isValidMonthTest() {
        assertFalse(ExpiryDateUtils.isValidMonth("20"))
        assertFalse(ExpiryDateUtils.isValidMonth("0"))
    }
}
