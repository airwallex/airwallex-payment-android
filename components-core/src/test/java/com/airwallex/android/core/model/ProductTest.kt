package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ProductTest {

    private val product by lazy {
        PhysicalProduct.Builder()
            .setCode("123")
            .setName("AirPods Pro")
            .setDesc("Buy AirPods Pro, per month with trade-in")
            .setSku("piece")
            .setType("Free engraving")
            .setUnitPrice(399.00)
            .setUrl("www.aircross.com")
            .setQuantity(1)
            .build()
    }

    @Test
    fun builderConstructor() {
        assertEquals(product, ProductFixtures.PRODUCT)
    }

    @Test
    fun testParcelable() {
        assertEquals(ProductFixtures.PRODUCT, ParcelUtils.create(ProductFixtures.PRODUCT))
    }

    @Test
    fun testParams() {
        assertEquals("123", product.code)
        assertEquals("AirPods Pro", product.name)
        assertEquals("Buy AirPods Pro, per month with trade-in", product.desc)
        assertEquals("piece", product.sku)
        assertEquals("Free engraving", product.type)
        assertEquals(399.00, product.unitPrice)
        assertEquals("www.aircross.com", product.url)
        assertEquals(1, product.quantity)
    }
}
