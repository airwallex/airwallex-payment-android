package com.airwallex.android.googlepay

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.airwallex.android.core.*
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.*
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeResponseParser
import com.airwallex.android.threedsecurity.AirwallexSecurityConnector
import com.airwallex.android.threedsecurity.ThreeDSecurityManager
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.*
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
    private lateinit var mockTask: Task<PaymentData>

    private val mockResponse: AvailablePaymentMethodTypeResponse =
        AvailablePaymentMethodTypeResponseParser().parse(
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
        mockkObject(AnalyticsLogger)
        mockkStatic(PaymentDataRequest::class)
        mockkStatic(PaymentsUtil::class)
        mockkStatic(Wallet::class)
        mockkStatic(PaymentsClient::class)
        mockkStatic(SystemClock::class)
        mockkStatic(PaymentData::class)
        mockkStatic(AutoResolveHelper::class)
        mockkObject(ThreeDSecurityManager)
        mockkConstructor(AirwallexSecurityConnector::class)
        component = GooglePayComponent()

        val session = mockk<AirwallexSession>(relaxed = true)
        val mockPaymentType = mockk<AvailablePaymentMethodType>()
        val mockResolver =
            mockk<(task: Task<PaymentData>, activity: Activity, requestCode: Int) -> Unit>(relaxed = true)
        component.resolvePaymentRequest = mockResolver
        component.session = session
        component.paymentMethodType = mockResponse.items?.first() ?: mockPaymentType
        activity = mockk()
        context = mockk()
        val mockClient = mockk<PaymentsClient>()
        val mockRequest = mockk<PaymentDataRequest>()
        listener = mockk(relaxed = true)
        mockTask = mockk()
        every { PaymentDataRequest.fromJson(any()) } returns mockRequest
        every { mockClient.loadPaymentData(mockRequest) } returns mockTask
        every { PaymentsUtil.createPaymentsClient(activity) } returns mockClient
    }

    @After
    fun unmockStatics() {
        unmockkObject(AnalyticsLogger)
        unmockkStatic(PaymentDataRequest::class)
        unmockkStatic(PaymentsUtil::class)
        unmockkStatic(Wallet::class)
        unmockkStatic(PaymentsClient::class)
        unmockkStatic(SystemClock::class)
        unmockkStatic(PaymentData::class)
        unmockkStatic(AutoResolveHelper::class)
        unmockkObject(ThreeDSecurityManager)
        unmockkConstructor(AirwallexSecurityConnector::class)
    }

    @Test
    fun `test handlePaymentIntentResponse when payment data request is valid`() {
        handlePaymentIntentResponse()
        verify(exactly = 1) { component.resolvePaymentRequest(mockTask, activity, 991) }
    }

    @Test
    fun `test handlePaymentIntentResponse when next action is redirect without card model`() {
        val redirectAction = NextAction(
            type = NextAction.NextActionType.REDIRECT_FORM,
            data = null,
            dcc = null,
            url = null,
            method = null
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
            method = null
        )
        val cardModel = CardNextActionModel(
            fragment = null,
            activity = activity,
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
                redirectAction,
                cardModel,
                listener
            )
        }
    }

    @Test
    fun `test handleActivityResult when request code is not loadPaymentDataRequestCode`() {
        assertFalse(component.handleActivityResult(1000, 0, null))
    }

    @Test
    fun `test handleActivityResult when result code is cancelled`() {
        handlePaymentIntentResponse()
        component.handleActivityResult(991, RESULT_CANCELED, null)
        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.Cancel) }
        verify(exactly = 1) { AnalyticsLogger.logPageView("google_pay_sheet", mapOf("code" to RESULT_CANCELED)) }
    }

    @Test
    fun `test handleActivityResult when result code is error`() {
        val intentData = mockk<Intent>()
        every { SystemClock.elapsedRealtime() } returns 0
        every { AutoResolveHelper.getStatusFromIntent(intentData) } returns Status.RESULT_INTERNAL_ERROR
        handlePaymentIntentResponse()
        component.handleActivityResult(991, AutoResolveHelper.RESULT_ERROR, intentData)
        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.Cancel) }
        verify(exactly = 1) {
            AnalyticsLogger.logError(
                "googlepay_payment_data_retrieve",
                mapOf("code" to Status.RESULT_INTERNAL_ERROR.statusCode.toString())
            )
        }
    }

    @Test
    fun `test handleActivityResult when result code is OK with intent data`() {
        val mockResponseJson =
            "{\"paymentMethodData\":{\"info\":{\"billingAddress\":{\"address1\":\"10 Collins St\"," +
                    "\"address2\":\"\",\"address3\":\"\",\"administrativeArea\":\"VIC\",\"countryCode\":\"AU\"," +
                    "\"locality\":\"Melbourne\",\"name\":\"John Citizen\",\"postalCode\":\"3000\"," +
                    "\"sortingCode\":\"\"}},\"tokenizationData\":{\"token\":\"MEUCIAzbCIvhBuBvH3Pz\"}}}"
        val mockData = mockk<Intent>()
        val paymentData = mockk<PaymentData>()
        every { paymentData.toJson() } returns mockResponseJson
        every { PaymentData.getFromIntent(mockData) } returns paymentData

        handlePaymentIntentResponse()
        component.handleActivityResult(991, RESULT_OK, mockData)
        val billing = Billing.Builder().setAddress(
            Address.Builder()
                .setCity("Melbourne")
                .setCountryCode("AU")
                .setPostcode("3000")
                .setState("VIC")
                .setStreet("10 Collins St")
                .build()
        )
            .setFirstName("John")
            .setLastName("Citizen")
            .build()
        verify {
            listener.onCompleted(
                AirwallexPaymentStatus.Success(
                    "id",
                    mapOf(
                        "payment_data_type" to "encrypted_payment_token",
                        "encrypted_payment_token" to "MEUCIAzbCIvhBuBvH3Pz",
                        "billing" to billing
                    )
                )
            )
        }
    }

    @Test
    fun `test handleActivityResult when result code is OK without intent data`() {
        handlePaymentIntentResponse()
        component.handleActivityResult(991, RESULT_OK, null)
        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.Cancel) }
    }

    @Test
    fun `test handleActivityResult when result code is OK with invalid intent data`() {
        val mockResponseJson = "adfqrdasf"
        val mockData = mockk<Intent>()
        val paymentData = mockk<PaymentData>()
        every { paymentData.toJson() } returns mockResponseJson
        every { PaymentData.getFromIntent(mockData) } returns paymentData

        handlePaymentIntentResponse()
        component.handleActivityResult(991, RESULT_OK, null)
        verify(exactly = 1) { listener.onCompleted(AirwallexPaymentStatus.Cancel) }
    }

    @Test
    fun `test retrieveSecurityToken`() {
        val securityListener = mockk<SecurityTokenListener>()
        val connector = MockKGateway.implementation().constructorMockFactory.mockPlaceholder(
            AirwallexSecurityConnector::class
        )
        every { connector.retrieveSecurityToken(any(), context, securityListener) } just runs
        component.retrieveSecurityToken("id", context, securityListener)
        verify(exactly = 1) { connector.retrieveSecurityToken(any(), context, securityListener) }
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