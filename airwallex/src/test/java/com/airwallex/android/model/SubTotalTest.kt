package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SubTotalTest {

    @Test
    fun builderConstructor() {
        val subTotal = SubTotal.Builder()
            .setFirstPrice("399")
            .setSecondPrice("469")
            .setFirstCount("1")
            .setSecondCount("1")
            .build()
        assertEquals("868", ""+subTotal.calculate())
    }

    @Test
    fun testParcelable() {
        assertEquals(SubTotalFixtures.SUBTOTAL, ParcelUtils.create(SubTotalFixtures.SUBTOTAL))
    }
}
