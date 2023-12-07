package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ThreeDSecureParamsTest {

    @Test
    fun testParcelable() {
        assertEquals(
            ThreeDSecureParesFixtures.THREEDSECUREPARES,
            ParcelUtils.create(ThreeDSecureParesFixtures.THREEDSECUREPARES)
        )
    }

    @Test
    fun testParams() {
        val threeDSecurePares = ThreeDSecureParesFixtures.THREEDSECUREPARES
        assertEquals("1", threeDSecurePares.paresId)
        assertEquals("render_qr_code", threeDSecurePares.pares)
    }
}
