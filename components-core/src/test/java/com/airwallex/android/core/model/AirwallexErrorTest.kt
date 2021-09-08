package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexErrorTest {

    @Test
    fun testParcelable() {
        assertEquals(AirwallexErrorFixtures.Error, ParcelUtils.create(AirwallexErrorFixtures.Error))
    }

    @Test
    fun testParams() {
        val error = AirwallexErrorFixtures.Error
        assertEquals("200", error.code)
        assertEquals("airwallex", error.source)
        assertEquals("success", error.message)
    }
}
