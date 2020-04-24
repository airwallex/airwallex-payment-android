package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ProductTest {
    @Test
    fun builderConstructor() {
        val product = PhysicalProduct.Builder()
            .setCode("123")
            .setName("AirPods Pro")
            .setDesc("Buy AirPods Pro, per month with trade-in")
            .setSku("piece")
            .setType("Free engraving")
            .setUnitPrice(399.00)
            .setUrl("www.aircross.com")
            .setQuantity(1)
            .build()
        assertEquals(product, ProductFixtures.PRODUCT)
    }

    @Test
    fun testParcelable() {
        assertEquals(ProductFixtures.PRODUCT, ParcelUtils.create(ProductFixtures.PRODUCT))
    }
}
