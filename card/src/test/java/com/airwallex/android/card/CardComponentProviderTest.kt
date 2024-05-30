package com.airwallex.android.card

import android.app.Activity
import android.content.Context
import com.airwallex.android.core.*
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import com.airwallex.android.core.model.parser.PageParser
import org.junit.Test
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import kotlin.test.assertTrue

@Suppress("NotNullAssertionOperatorRule")
class CardComponentProviderTest {
    private val mockResponse: Page<AvailablePaymentMethodType> =
        PageParser(AvailablePaymentMethodTypeParser()).parse(
            JSONObject(
                """
                    {
                    "items":[
                    {
                   "name":"card",
                   "card_schemes":[{ "name": "mastercard" }]
                    }   
                    ],
                    "has_more":false
                    }
                """.trimIndent()
            )
        )

    @Test
    fun onActivityResultTest() {
        val cardComponentProvider = CardComponentProvider()
        assertEquals(false, cardComponentProvider.get().handleActivityResult(1, 1, null))
    }

    @Test
    fun canHandleActionTest() {
        val cardComponentProvider = CardComponentProvider()
        assertEquals(
            true,
            cardComponentProvider.canHandleAction(
                NextAction(
                    stage = NextAction.NextActionStage.WAITING_USER_INFO_INPUT,
                    type = NextAction.NextActionType.DCC,
                    data = null,
                    dcc = null,
                    url = null,
                    method = null
                )
            )
        )
        assertEquals(
            true,
            cardComponentProvider.canHandleAction(
                NextAction(
                    stage = NextAction.NextActionStage.WAITING_USER_INFO_INPUT,
                    type = NextAction.NextActionType.REDIRECT,
                    data = mapOf("1" to "2"),
                    dcc = null,
                    url = null,
                    method = null
                )
            )
        )
        assertEquals(
            false,
            cardComponentProvider.canHandleAction(
                NextAction(
                    stage = NextAction.NextActionStage.WAITING_USER_INFO_INPUT,
                    type = NextAction.NextActionType.CALL_SDK,
                    data = null,
                    dcc = null,
                    url = null,
                    method = null
                )
            )
        )
    }

    @Test
    fun handlePaymentIntentResponseDccTest() {
        val cardComponentProvider = CardComponentProvider()

        var success = false

        val latch = CountDownLatch(1)
        val activity: Activity = mockk()
        val context: Context = mockk()
        cardComponentProvider.get().handlePaymentIntentResponse(
            "int_hkdmr7v9rg1j58ky8re",
            NextAction(
                stage = NextAction.NextActionStage.WAITING_USER_INFO_INPUT,
                type = NextAction.NextActionType.DCC,
                data = null,
                dcc = null,
                url = null,
                method = "POST"
            ),
            null,
            activity,
            context,
            CardNextActionModel(
                fragment = null,
                activity = activity,
                paymentManager = AirwallexPaymentManager(AirwallexApiRepository()),
                clientSecret = "tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
                device = null,
                paymentIntentId = "int_hkdmr7v9rg1j58ky8re",
                currency = "CNY",
                amount = BigDecimal.TEN
            ),
            object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    when (status) {
                        is AirwallexPaymentStatus.Success,
                        is AirwallexPaymentStatus.InProgress -> {
                            success = true
                            latch.countDown()
                        }
                        is AirwallexPaymentStatus.Failure -> {
                            success = false
                            latch.countDown()
                        }
                        else -> Unit
                    }
                }
            }
        )

        latch.await()
        assertEquals(false, success)
    }

    @Test
    fun canHandleSessionAndPaymentMethodTest() = runTest {
        val activity = mockk<Activity>()
        val session = mockk<AirwallexSession>()
        val paymentMethodType = mockResponse.items?.first()
        val cardComponentProvider = CardComponentProvider()
        assertTrue(
            cardComponentProvider.canHandleSessionAndPaymentMethod(
                session,
                paymentMethodType!!,
                activity
            )
        )
    }
}
