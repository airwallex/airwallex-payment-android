package com.airwallex.android.redirect

import androidx.appcompat.view.ContextThemeWrapper
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.PaymentMethodType
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals

class RedirectComponentProviderTest {

    private val context = ContextThemeWrapper()

    @Test
    fun canHandleActionTest() {
        val redirectComponentProvider = RedirectComponentProvider()
        assertEquals(true, redirectComponentProvider.canHandleAction(PaymentMethodType.ALIPAY_CN))
        assertEquals(false, redirectComponentProvider.canHandleAction(PaymentMethodType.CARD))
        assertEquals(false, redirectComponentProvider.canHandleAction(PaymentMethodType.WECHAT))
    }

    @Test
    fun handlePaymentIntentResponseTest() {
        val redirectComponentProvider = RedirectComponentProvider()

        var success = false
        val latch = CountDownLatch(1)
        redirectComponentProvider.handlePaymentIntentResponse(
            nextAction = NextAction(
                type = NextAction.NextActionType.REDIRECT,
                data = null,
                dcc = null,
                url = "https://cdn-psp.marmot-cloud.com/acwallet/alipayconnectcode?code=golcashier1629873426081sandbox&golSandbox=true&pspName=ALIPAY_CN",
                method = "GET"
            ),
            null,
            object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onFailed(exception: AirwallexException) {
                    latch.countDown()
                }

                override fun onNextActionWithRedirectUrl(url: String) {
                    success = true
                    latch.countDown()
                }
            }
        )

        latch.await()
        assertEquals(true, success)
    }

    @Test
    fun onActivityResultTest() {
        val redirectComponentProvider = RedirectComponentProvider()
        assertEquals(false, redirectComponentProvider.onActivityResult(1, 1, null))
    }

    @Test
    fun retrieveSecurityTokenTest() {
        val redirectComponentProvider = RedirectComponentProvider()

        val latch = CountDownLatch(1)
        var device = "11"
        redirectComponentProvider.retrieveSecurityToken(
            "11", context,
            object :
                SecurityTokenListener {
                override fun onResponse(deviceId: String) {
                    device = deviceId
                    latch.countDown()
                }
            }
        )
        latch.await()
        assertEquals("", device)
    }
}
