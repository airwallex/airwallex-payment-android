package com.airwallex.android.view

import com.airwallex.android.view.util.ExpiryDateUtils
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ExpiryDateUtilsTest {

    @Test
    fun isExpiryDataValidTest() {
        val calendar = Calendar.getInstance()
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
