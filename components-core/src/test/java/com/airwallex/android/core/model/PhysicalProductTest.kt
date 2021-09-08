package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PhysicalProductTest {

    private val physicalProduct by lazy {
        PhysicalProduct.Builder()
            .setCode("123")
            .setName("AirPods")
            .setDesc("Buy AirPods Pro, per month with trade-in")
            .setSku("piece")
            .setType("White")
            .setUnitPrice(500.00)
            .setUrl("www.aircross.com")
            .setQuantity(1)
            .build()
    }

    @Test
    fun builderConstructor() {
        assertEquals(physicalProduct, PhysicalProductFixtures.PHYSICAL_PRODUCT)
    }

    @Test
    fun testParams() {
        assertEquals("White", physicalProduct.type)
        assertEquals("123", physicalProduct.code)
        assertEquals("AirPods", physicalProduct.name)
        assertEquals("piece", physicalProduct.sku)
        assertEquals(1, physicalProduct.quantity)
        assertEquals(500.00, physicalProduct.unitPrice)
        assertEquals("Buy AirPods Pro, per month with trade-in", physicalProduct.desc)
        assertEquals("www.aircross.com", physicalProduct.url)
    }

    @Test
    fun testToParamsMap() {
        val paramMap = physicalProduct.toParamMap()
        assertEquals(
            mapOf(
                "type" to "White",
                "code" to "123",
                "name" to "AirPods",
                "sku" to "piece",
                "quantity" to 1,
                "unit_price" to 500.0,
                "desc" to "Buy AirPods Pro, per month with trade-in",
                "url" to "www.aircross.com"
            ),
            paramMap
        )
    }
}
