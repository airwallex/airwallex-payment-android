package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

    @Test
    fun `test toParamMap with all null fields`() {
        val product = PhysicalProduct.Builder().build()
        val paramMap = product.toParamMap()

        assertEquals(emptyMap(), paramMap)
        assertNull(product.type)
        assertNull(product.code)
        assertNull(product.name)
        assertNull(product.sku)
        assertNull(product.quantity)
        assertNull(product.unitPrice)
        assertNull(product.desc)
        assertNull(product.url)
    }

    @Test
    fun `test toParamMap with only code set`() {
        val product = PhysicalProduct.Builder()
            .setCode("ABC123")
            .build()

        val paramMap = product.toParamMap()

        assertEquals(mapOf("code" to "ABC123"), paramMap)
        assertFalse(paramMap.containsKey("type"))
        assertFalse(paramMap.containsKey("name"))
    }
}
