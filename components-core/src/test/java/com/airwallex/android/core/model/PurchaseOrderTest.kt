package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PurchaseOrderTest {

    @Test
    fun testParcelable() {
        assertEquals(
            PurchaseOrderFixtures.PURCHASEORDER,
            ParcelUtils.create(PurchaseOrderFixtures.PURCHASEORDER)
        )
    }
}