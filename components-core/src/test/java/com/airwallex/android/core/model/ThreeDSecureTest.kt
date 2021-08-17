package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ThreeDSecureTest {

    @Test
    fun testParcelable() {
        assertEquals(
            ThreeDSecureFixtures.THREEDSECURE,
            ParcelUtils.createMaybeNull(ThreeDSecureFixtures.THREEDSECURE)
        )
    }

    @Test
    fun testParams() {
        val threeDSecure = ThreeDSecureFixtures.THREEDSECURE!!
        assertEquals("https://www.airwallex.com", threeDSecure.returnUrl)
        assertEquals("abc", threeDSecure.deviceDataCollectionRes)
        assertEquals("123", threeDSecure.transactionId)
    }
}
