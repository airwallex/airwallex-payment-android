package com.airwallex.android.googlepay

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.*
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import com.airwallex.android.core.model.parser.PageParser
import com.airwallex.android.threedsecurity.AirwallexSecurityConnector
import com.airwallex.android.threedsecurity.ThreeDSecurityActivityLaunch
import com.airwallex.android.threedsecurity.ThreeDSecurityManager
import com.airwallex.android.ui.extension.getExtraResult
import io.mockk.*
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class GooglePayComponentTest {
    private lateinit var component: GooglePayComponent
    private lateinit var activity: Activity
    private lateinit var context: Context
    private lateinit var listener: Airwallex.PaymentResultListener

    private val mockResponse: Page<AvailablePaymentMethodType> =
        PageParser(AvailablePaymentMethodTypeParser()).parse(
            JSONObject(
                """
                    {
                    "items":[
                    {
                   "name":"googlepay",
                   "transaction_mode":"oneoff",
                   "active":true,
                   "transaction_currencies":["dollar","RMB"],
                   "flows":["inapp"],
                   "card_schemes":[{ "name": "mastercard" }]
                    }   
                    ],
                    "has_more":false
                    }
                """.trimIndent()
            )
        )

    @Before
    fun setUp() {
        mockkObject(ThreeDSecurityManager)
        mockkConstructor(AirwallexSecurityConnector::class)
        mockkConstructor(GooglePayActivityLaunch::class)
        component = GooglePayComponent()

        every { anyConstructed<GooglePayActivityLaunch>().startForResult(any()) } just runs
        every { anyConstructed<GooglePayActivityLaunch>().launchForResult(any(), any()) } just runs
        val session = mockk<AirwallexSession>(relaxed = true)
        val mockPaymentType = mockk<AvailablePaymentMethodType>()
        component.session = session
        component.paymentMethodType = mockResponse.items.first() ?: mockPaymentType
        activity = mockk()
        context = mockk()
        listener = mockk(relaxed = true)
    }

    @After
    fun unmockStatics() {
        unmockkObject(ThreeDSecurityManager)
        unmockkConstructor(AirwallexSecurityConnector::class)
        unmockkConstructor(GooglePayActivityLaunch::class)
    }

    @Test
    fun `test handlePaymentIntentResponse when next action is redirect without card model`() {
        val redirectAction = NextAction(
            type = NextAction.NextActionType.REDIRECT_FORM,
            data = null,
            dcc = null,
            url = null,
            method = null,
            packageName = null
        )
        handlePaymentIntentResponse(action = redirectAction)
        verify(exactly = 1) { listener.onCompleted(any<AirwallexPaymentStatus.Failure>()) }
    }

    @Test
    fun `test handlePaymentIntentResponse when next action is redirect with card model`() {
        val redirectAction = NextAction(
            type = NextAction.NextActionType.REDIRECT_FORM,
            data = null,
            dcc = null,
            url = null,
            method = null,
            packageName = null
        )
        val cardModel = CardNextActionModel(
            paymentManager = AirwallexPaymentManager(AirwallexApiRepository()),
            clientSecret = "tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
            device = null,
            paymentIntentId = "int_hkdmr7v9rg1j58ky8re",
            currency = "CNY",
            amount = BigDecimal.TEN
        )
        handlePaymentIntentResponse(redirectAction, cardModel)
        verify(exactly = 1) {
            ThreeDSecurityManager.handleThreeDSFlow(
                "id",
                activity,
                null,
                redirectAction,
                cardModel,
                listener,
                resultCallBack = any()
            )
        }
    }

    @Test
    fun `test handleActivityResult when request code is not loadPaymentDataRequestCode`() {
        assertFalse(component.handleActivityResult(1000, 0, null))
    }

    @Test
    fun `test handleActivityResult when listener is null`() {
        val intent = mockk<Intent>()
        assertFalse(component.handleActivityResult(1006, RESULT_OK, intent))
    }

    @Test
    fun `test handleActivityResult when result is 3ds success`() {
        val intent = mockk<Intent>()
        val result = mockk<ThreeDSecurityActivityLaunch.Result>(relaxed = true)
        every { result.paymentIntentId } returns "intentId"
        every { intent.getExtraResult<ThreeDSecurityActivityLaunch.Result>() } returns result

        handlePaymentIntentResponse()
        assert(component.handleActivityResult(1006, RESULT_OK, intent))
        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.Success("intentId")) }
    }

    @Test
    fun `test handleActivityResult when result is 3ds fail`() {
        val intent = mockk<Intent>()
        val result = mockk<ThreeDSecurityActivityLaunch.Result>(relaxed = true)
        val exception = mockk<AirwallexException>()
        every { result.exception } returns exception
        every { intent.getExtraResult<ThreeDSecurityActivityLaunch.Result>() } returns result

        handlePaymentIntentResponse()
        assert(component.handleActivityResult(1006, RESULT_OK, intent))
        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.Failure(exception)) }
    }

    @Test
    fun `test handleActivityResult when result is google pay cancel`() {
        val intent = mockk<Intent>()
        every { intent.getExtraResult<GooglePayActivityLaunch.Result>() } returns GooglePayActivityLaunch.Result.Cancel

        handlePaymentIntentResponse()
        assert(component.handleActivityResult(1007, RESULT_OK, intent))
        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.Cancel) }
    }

    @Test
    fun `test handleActivityResult when result is google pay fail`() {
        val intent = mockk<Intent>()
        val exception = mockk<AirwallexException>()
        every { intent.getExtraResult<GooglePayActivityLaunch.Result>() } returns GooglePayActivityLaunch.Result.Failure(
            exception
        )

        handlePaymentIntentResponse()
        assert(component.handleActivityResult(1007, RESULT_OK, intent))
        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.Failure(exception)) }
    }

    @Test
    fun `test handleActivityResult when result is google pay success`() {
        val intent = mockk<Intent>()
        val map = mockk<Map<String, Any>>()
        every { intent.getExtraResult<GooglePayActivityLaunch.Result>() } returns GooglePayActivityLaunch.Result.Success(
            map
        )

        handlePaymentIntentResponse()
        assert(component.handleActivityResult(1007, RESULT_OK, intent))
        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.Success("id", null, map)) }
    }

    @Test
    fun `test retrieveSecurityToken`() {
        val securityListener = mockk<SecurityTokenListener>()
        val connector = MockKGateway.implementation().constructorMockFactory.mockPlaceholder(
            AirwallexSecurityConnector::class
        )
        every { connector.retrieveSecurityToken(any(), securityListener) } just runs
        component.retrieveSecurityToken("id", securityListener)
        verify(exactly = 1) { connector.retrieveSecurityToken(any(), securityListener) }
    }

    private fun handlePaymentIntentResponse(
        action: NextAction? = null,
        model: CardNextActionModel? = null
    ) {
        component.handlePaymentIntentResponse(
            paymentIntentId = "id",
            nextAction = action,
            activity = activity,
            applicationContext = context,
            cardNextActionModel = model,
            listener = listener
        )
    }
}