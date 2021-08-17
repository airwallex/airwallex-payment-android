package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class OrderTest {

    private val shipping: Shipping by lazy {
        Shipping.Builder()
            .setFirstName("John")
            .setLastName("Doe")
            .setPhone("13800000000")
            .setAddress(
                Address.Builder()
                    .setCountryCode("CN")
                    .setState("Shanghai")
                    .setCity("Shanghai")
                    .setStreet("Pudong District")
                    .setPostcode("100000")
                    .build()
            )
            .build()
    }

    private val products by lazy {
        mutableListOf(
            PhysicalProduct.Builder()
                .setCode("123")
                .setName("AirPods Pro")
                .setDesc("Buy AirPods Pro, per month with trade-in")
                .setSku("piece")
                .setType("Free engraving")
                .setUnitPrice(399.00)
                .setUrl("www.aircross.com")
                .setQuantity(1)
                .build(),
            PhysicalProduct.Builder()
                .setCode("123")
                .setName("HomePod")
                .setDesc("Buy HomePod, per month with trade-in")
                .setSku("piece")
                .setType("White")
                .setUnitPrice(469.00)
                .setUrl("www.aircross.com")
                .setQuantity(1)
                .build()
        )
    }

    private val order by lazy {
        PurchaseOrder.Builder()
            .setProducts(products)
            .setShipping(shipping)
            .setType("physical_goods")
            .build()
    }

    @Test
    fun builderConstructor() {
        assertEquals(order, OrderFixtures.ORDER)
    }

    @Test
    fun testParcelable() {
        assertEquals(OrderFixtures.ORDER, ParcelUtils.create(OrderFixtures.ORDER))
    }

    @Test
    fun testParams() {
        assertEquals(products, order.products)
        assertEquals(shipping, order.shipping)
        assertEquals("physical_goods", order.type)
    }
}
