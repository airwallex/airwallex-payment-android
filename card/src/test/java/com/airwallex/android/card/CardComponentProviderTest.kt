package com.airwallex.android.card

import android.app.Activity
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.core.*
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
        val activity: Activity = mock()
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
}
