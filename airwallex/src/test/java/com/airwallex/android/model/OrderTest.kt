package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class OrderTest {
    @Test
    fun builderConstructor() {
        val shipping: Shipping = Shipping.Builder()
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
        val products = mutableListOf(
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
        val order = PurchaseOrder.Builder()
            .setProducts(products)
            .setShipping(shipping)
            .setType("physical_goods")
            .build()
        assertEquals(order, OrderFixtures.ORDER)
    }

    @Test
    fun testParcelable() {
        assertEquals(OrderFixtures.ORDER, ParcelUtils.create(OrderFixtures.ORDER))
    }
}
