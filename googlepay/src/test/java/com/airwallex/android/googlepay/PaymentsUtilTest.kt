package com.airwallex.android.googlepay

import com.airwallex.android.core.BillingAddressParameters
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.ShippingAddressParameters
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.CardScheme
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PaymentsUtilTest {
    @Test
    fun `test isReadyToPayRequest without supported card schemes`() {
        val request = PaymentsUtil.isReadyToPayRequest(
            GooglePayOptions(), null
        )
        assertEquals(
            request.toString(),
            "{\"apiVersionMinor\":0,\"apiVersion\":2,\"allowedPaymentMethods\":" +
                    "[{\"type\":\"CARD\",\"parameters\":{\"cvcRequired\":true,\"allowedAuthMethods\":[\"PAN_ONLY\"," +
                    "\"CRYPTOGRAM_3DS\"],\"allowedCardNetworks\":[\"AMEX\",\"DISCOVER\",\"JCB\",\"MASTERCARD\",\"VISA\"]}}]}"
        )
    }

    @Test
    fun `test isReadyToPayRequest with supported card schemes`() {
        val request = PaymentsUtil.isReadyToPayRequest(
            GooglePayOptions(),
            listOf(CardScheme("mastercard"))
        )
        assertEquals(
            request.toString(),
            "{\"apiVersionMinor\":0,\"apiVersion\":2,\"allowedPaymentMethods\":" +
                    "[{\"type\":\"CARD\",\"parameters\":{\"cvcRequired\":true,\"allowedAuthMethods\":[\"PAN_ONLY\"," +
                    "\"CRYPTOGRAM_3DS\"],\"allowedCardNetworks\":[\"MASTERCARD\"]}}]}"
        )
    }

    @Test
    fun `test isReadyToPayRequest with billing address params and other requirements`() {
        val request = PaymentsUtil.isReadyToPayRequest(
            GooglePayOptions(
                allowPrepaidCards = false,
                allowCreditCards = false,
                assuranceDetailsRequired = true,
                billingAddressRequired = true,
                billingAddressParameters = BillingAddressParameters(
                    BillingAddressParameters.Format.FULL, true
                )
            ),
            null
        )
        assertEquals(
            request.toString(),
            "{\"apiVersionMinor\":0,\"apiVersion\":2,\"allowedPaymentMethods\":" +
                    "[{\"type\":\"CARD\",\"parameters\":{\"cvcRequired\":true,\"assuranceDetailsRequired\":true," +
                    "\"allowedAuthMethods\":[\"PAN_ONLY\",\"CRYPTOGRAM_3DS\"],\"billingAddressRequired\":true," +
                    "\"billingAddressParameters\":{\"format\":\"FULL\",\"phoneNumberRequired\":true}," +
                    "\"allowedCardNetworks\":[\"AMEX\",\"DISCOVER\",\"JCB\",\"MASTERCARD\",\"VISA\"],\"allowCreditCards\":false," +
                    "\"allowPrepaidCards\":false}}]}"
        )
    }

    @Test
    fun `test getPaymentDataRequest`() {
        val request = PaymentsUtil.getPaymentDataRequest(
            price = BigDecimal.valueOf(100.01),
            countryCode = "AU",
            currency = "AUD",
            googlePayOptions = GooglePayOptions(
                merchantName = "Some Merchant",
                transactionId = "zcvrwf14r1",
                checkoutOption = "COMPLETE_IMMEDIATE_PURCHASE",
                emailRequired = true,
                shippingAddressParameters = ShippingAddressParameters(
                    listOf("US", "CN"),
                    true
                )
            ),
            supportedCardSchemes = listOf(CardScheme("mastercard"), CardScheme("visa"))
        )
        assertEquals(
            request.toString(),
            "{\"apiVersionMinor\":0,\"apiVersion\":2,\"merchantInfo\":{\"merchantName\":\"Some Merchant\"}," +
                    "\"allowedPaymentMethods\":[{\"type\":\"CARD\",\"parameters\":{\"cvcRequired\":true," +
                    "\"allowedAuthMethods\":[\"PAN_ONLY\",\"CRYPTOGRAM_3DS\"],\"allowedCardNetworks\":" +
                    "[\"MASTERCARD\",\"VISA\"]},\"tokenizationSpecification\":{\"type\":\"PAYMENT_GATEWAY\"," +
                    "\"parameters\":{\"gatewayMerchantId\":\"\",\"gateway\":\"airwallex\"}}}]," +
                    "\"shippingAddressParameters\":{\"allowedCountryCodes\":[\"US\",\"CN\"]," +
                    "\"phoneNumberRequired\":true},\"emailRequired\":true,\"transactionInfo\":" +
                    "{\"totalPrice\":\"100.01\",\"countryCode\":\"AU\",\"totalPriceLabel\":\"order.total\"," +
                    "\"checkoutOption\":\"COMPLETE_IMMEDIATE_PURCHASE\",\"totalPriceStatus\":\"FINAL\"," +
                    "\"currencyCode\":\"AUD\",\"transactionId\":\"zcvrwf14r1\"}}"
        )
    }

    @Test
    fun `test getBilling`() {
        val json = JSONObject(
            """
                {
                "address1":"10 Collins St",
                "address3":"Unit 4214",
                "administrativeArea":"VIC",
                "countryCode":"AU",
                "locality":"Melbourne",
                "name":"John Citizen",
                "postalCode":"3000",
                "sortingCode":""
                }
            """.trimIndent()
        )
        assertEquals(
            PaymentsUtil.getBilling(json),
            Billing.Builder().setAddress(
                Address.Builder()
                    .setCity("Melbourne")
                    .setCountryCode("AU")
                    .setPostcode("3000")
                    .setState("VIC")
                    .setStreet("10 Collins St Unit 4214")
                    .build()
                )
            .setFirstName("John")
            .setLastName("Citizen")
            .build()
        )
    }

    @Test
    fun `test getBilling when name does not have space`() {
        val json = JSONObject(
            """
                {
                "address2":"Unit 4214",
                "administrativeArea":"VIC",
                "countryCode":"AU",
                "locality":"Melbourne",
                "name":"John"
                }
            """.trimIndent()
        )
        assertEquals(PaymentsUtil.getBilling(json)?.firstName, "John")
        assertEquals(PaymentsUtil.getBilling(json)?.lastName, "")
    }

    @Test
    fun `test getBilling city when locality is empty`() {
        val json = JSONObject(
            """
                {
                "countryCode":"HK",
                "locality":"",
                "name":"John"
                }
            """.trimIndent()
        )
        assertEquals(PaymentsUtil.getBilling(json)?.address?.city, "HK")
    }
}
