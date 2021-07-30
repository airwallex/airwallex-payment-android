package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AirwallexErrorTest {

    @Test
    fun testParcelable() {
        assertEquals(AirwallexErrorFixtures.Error, ParcelUtils.create(AirwallexErrorFixtures.Error))
    }
}
