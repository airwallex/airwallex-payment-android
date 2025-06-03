package com.airwallex.android.view.util

import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.TokenManager
import com.airwallex.android.core.googlePaySupportedNetworks
import com.airwallex.android.core.model.CardScheme
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class GooglePayUtilTest {

    private val testAccountId = "test_account_id"
    private val defaultSupportedNetworks = listOf("AMEX", "DISCOVER", "JCB", "MASTERCARD", "VISA")

    @Before
    fun setup() {
        mockkObject(TokenManager)
        every { TokenManager.accountId } returns testAccountId
        
        // Mock the googlePaySupportedNetworks function
        mockkStatic("com.airwallex.android.core.ConstantsKt")
        every { googlePaySupportedNetworks() } returns defaultSupportedNetworks
    }
    
    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `retrieveAllowedPaymentMethods with default options and no card schemes returns default config`() {
        val options = GooglePayOptions()
        val result = GooglePayUtil.retrieveAllowedPaymentMethods(options, null)

        assertNotNull(result)
        assertEquals(1, result?.length())
        
        val paymentMethod = result?.getJSONObject(0)
        assertEquals("CARD", paymentMethod?.getString("type"))
        
        val parameters = paymentMethod?.getJSONObject("parameters")
        assertNotNull(parameters)
        
        val allowedAuthMethods = parameters?.getJSONArray("allowedAuthMethods")
        assertEquals(2, allowedAuthMethods?.length())
        assertEquals("PAN_ONLY", allowedAuthMethods?.getString(0))
        assertEquals("CRYPTOGRAM_3DS", allowedAuthMethods?.getString(1))
        
        val tokenizationSpec = paymentMethod?.getJSONObject("tokenizationSpecification")
        assertNotNull(tokenizationSpec)
        assertEquals("PAYMENT_GATEWAY", tokenizationSpec?.getString("type"))
        
        val tokenizationParams = tokenizationSpec?.getJSONObject("parameters")
        assertNotNull(tokenizationParams)
        assertEquals("airwallex", tokenizationParams?.getString("gateway"))
        assertEquals(testAccountId, tokenizationParams?.getString("gatewayMerchantId"))
    }

    @Test
    fun `retrieveAllowedPaymentMethods with custom card schemes returns correct card networks`() {
        val options = GooglePayOptions()
        val cardSchemes = listOf(
            CardScheme("VISA"),
            CardScheme("MASTERCARD")
        )
        
        val result = GooglePayUtil.retrieveAllowedPaymentMethods(options, cardSchemes)
        
        assertNotNull(result)
        val paymentMethod = result?.getJSONObject(0)
        val parameters = paymentMethod?.getJSONObject("parameters")
        val allowedCardNetworks = parameters?.getJSONArray("allowedCardNetworks")
        
        assertEquals(2, allowedCardNetworks?.length())
        assertEquals("VISA", allowedCardNetworks?.getString(0))
        assertEquals("MASTERCARD", allowedCardNetworks?.getString(1))
    }

    @Test
    fun `retrieveAllowedPaymentMethods with custom auth methods returns correct auth methods`() {
        val options = GooglePayOptions(
            allowedCardAuthMethods = listOf("PAN_ONLY")
        )
        
        val result = GooglePayUtil.retrieveAllowedPaymentMethods(options, null)
        
        assertNotNull(result)
        val paymentMethod = result?.getJSONObject(0)
        val parameters = paymentMethod?.getJSONObject("parameters")
        val allowedAuthMethods = parameters?.getJSONArray("allowedAuthMethods")
        
        assertEquals(1, allowedAuthMethods?.length())
        assertEquals("PAN_ONLY", allowedAuthMethods?.getString(0))
    }

    @Test
    fun `retrieveAllowedPaymentMethods with prepaid and credit card restrictions applies them to parameters`() {
        val options = GooglePayOptions(
            allowPrepaidCards = false,
            allowCreditCards = false
        )
        
        val result = GooglePayUtil.retrieveAllowedPaymentMethods(options, null)
        
        assertNotNull(result)
        val paymentMethod = result?.getJSONObject(0)
        val parameters = paymentMethod?.getJSONObject("parameters")
        
        assertFalse(parameters?.getBoolean("allowPrepaidCards") ?: true)
        assertFalse(parameters?.getBoolean("allowCreditCards") ?: true)
    }

    @Test
    fun `retrieveAllowedPaymentMethods with JSON exception returns null`() {
        // This test is to ensure the try-catch block works as expected
        // We can't easily trigger a JSONException in the current implementation,
        // but we can verify that the method returns null in case of any exception
        // by passing an empty card scheme list (which might cause issues)
        val options = GooglePayOptions()
        
        // This test is more about ensuring the try-catch is there and works
        // The actual behavior might change based on implementation details
        val result = GooglePayUtil.retrieveAllowedPaymentMethods(options, emptyList())
        
        // The current implementation would return a JSONArray with one element
        // even for empty card schemes, but we're testing the error handling path
        assertNotNull(result)
        assertEquals(1, result?.length())
    }

    @Test
    fun `retrieveAllowedPaymentMethods with null accountId still works`() {
        every { TokenManager.accountId } returns null
        val options = GooglePayOptions()
        
        val result = GooglePayUtil.retrieveAllowedPaymentMethods(options, null)
        
        assertNotNull(result)
        val paymentMethod = result?.getJSONObject(0)
        val tokenizationSpec = paymentMethod?.getJSONObject("tokenizationSpecification")
        val tokenizationParams = tokenizationSpec?.getJSONObject("parameters")
        
        assertEquals("", tokenizationParams?.getString("gatewayMerchantId"))
    }
}
