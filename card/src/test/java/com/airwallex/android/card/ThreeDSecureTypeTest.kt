package com.airwallex.android.card

import org.junit.Test
import kotlin.test.assertEquals

class ThreeDSecureTypeTest {

    @Test
    fun valueTest() {
        assertEquals("THREE_D_SECURE_1", ThreeDSecureManager.ThreeDSecureType.THREE_D_SECURE_1.name)
        assertEquals("THREE_D_SECURE_2", ThreeDSecureManager.ThreeDSecureType.THREE_D_SECURE_2.name)
    }
}
