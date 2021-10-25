package com.airwallex.android.card

import android.app.Activity
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexApiRepository
import com.airwallex.android.core.AirwallexPaymentManager
import com.airwallex.android.core.CardNextActionModel
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.NextAction
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals
import com.nhaarman.mockitokotlin2.mock

@RunWith(RobolectricTestRunner::class)
class CardComponentProviderTest {

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
    fun handlePaymentIntentResponse3DSTest() {
        val cardComponentProvider = CardComponentProvider()

        var success = false

        val latch = CountDownLatch(1)
        val activity: Activity = mock()

        try {
            cardComponentProvider.get().handlePaymentIntentResponse(
                "int_hkdmr7v9rg1j58ky8re",
                NextAction(
                    type = NextAction.NextActionType.REDIRECT,
                    data = mapOf(
                        "jwt" to "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI5ZjVkNmNjZC0zNTAxLTQzYzEtOGU2Yy01YTI4ZDM4ODA5ZjAiLCJpYXQiOjE2Mjk4NzcwNDIsImlzcyI6IjVlOWQ5ZmI2MTI1MzdjMzBhYzdlYjJhOCIsIk9yZ1VuaXRJZCI6IjVlOWQ5ZmI2YmUwZTg2MzQ3ZjYwNjA5YSIsIlJldHVyblVybCI6Imh0dHBzOi8vd3d3LmFpcndhbGxleC5jb20iLCJPYmplY3RpZnlQYXlsb2FkIjpmYWxzZX0.tpLx6wv8hYzMI85i-bVyqKQnmCSt-qPV0GNaA74ofQs",
                        "stage" to "WAITING_DEVICE_DATA_COLLECTION"
                    ),
                    dcc = null,
                    url = "https://api-demo.airwallex.com/api/v1/pa/card3ds-mock/fingerprint",
                    method = "POST"
                ),
                activity,
                ApplicationProvider.getApplicationContext(),
                CardNextActionModel(
                    fragment = null,
                    activity = activity,
                    paymentManager = AirwallexPaymentManager(AirwallexApiRepository()),
                    clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
                    device = null,
                    paymentIntentId = "int_hkdmr7v9rg1j58ky8re",
                    currency = "CNY",
                    amount = BigDecimal.TEN
                ),
                object : Airwallex.PaymentResultListener {
                    override fun onSuccess(paymentIntentId: String, isRedirecting: Boolean) {
                        success = true
                        latch.countDown()
                    }

                    override fun onFailed(exception: AirwallexException) {
                        success = false
                        latch.countDown()
                    }
                }
            )
        } catch (e: Exception) {
            success = false
            latch.countDown()
        }

        latch.await()
        assertEquals(false, success)
    }

    @Test
    fun handlePaymentIntentResponseDccTest() {
        val cardComponentProvider = CardComponentProvider()

        var success = false

        val latch = CountDownLatch(1)
        val activity: Activity = mock()
        cardComponentProvider.get().handlePaymentIntentResponse(
            "int_hkdmr7v9rg1j58ky8re",
            NextAction(
                type = NextAction.NextActionType.DCC,
                data = null,
                dcc = null,
                url = null,
                method = "POST"
            ),
            activity,
            ApplicationProvider.getApplicationContext(),
            CardNextActionModel(
                fragment = null,
                activity = activity,
                paymentManager = AirwallexPaymentManager(AirwallexApiRepository()),
                clientSecret = "ap4Uep2dv31m0UKP4-UkPsdTlvxUR2ecjRLdqaPNYpdGUPjBOuGysGc_AtbfuNn1lnLCU5mNDhZWgNvm0l-tuBvO8EeCuC90RVHzG_vQXhDafnDiySTFW-cMlK-tqj9uJlZZ8NIFEM_dpZb2DXbGkQ==",
                device = null,
                paymentIntentId = "int_hkdmr7v9rg1j58ky8re",
                currency = "CNY",
                amount = BigDecimal.TEN
            ),
            object : Airwallex.PaymentResultListener {
                override fun onSuccess(paymentIntentId: String, isRedirecting: Boolean) {
                    success = true
                    latch.countDown()
                }

                override fun onFailed(exception: AirwallexException) {
                    success = false
                    latch.countDown()
                }
            }
        )

        latch.await()
        assertEquals(false, success)
    }
}
