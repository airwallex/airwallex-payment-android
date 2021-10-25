package com.airwallex.android.redirect

import android.app.Activity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.NextAction
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals
import com.nhaarman.mockitokotlin2.mock
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RedirectComponentProviderTest {

    private val context = ContextThemeWrapper()

    @Test
    fun canHandleActionTest() {
        val redirectComponentProvider = RedirectComponentProvider()
        assertEquals(
            false,
            redirectComponentProvider.canHandleAction(
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
            true,
            redirectComponentProvider.canHandleAction(
                NextAction(
                    type = NextAction.NextActionType.REDIRECT,
                    data = null,
                    dcc = null,
                    url = null,
                    method = null
                )
            )
        )
        assertEquals(false, redirectComponentProvider.canHandleAction(null))
    }

    @Test
    fun handlePaymentIntentResponseTest() {
        val redirectComponentProvider = RedirectComponentProvider()

        var success = false
        val latch = CountDownLatch(1)
        val activity: Activity = mock()

        try {
            redirectComponentProvider.get().handlePaymentIntentResponse(
                "int_hkdmr7v9rg1j58ky8re",
                nextAction = NextAction(
                    type = NextAction.NextActionType.REDIRECT,
                    data = null,
                    dcc = null,
                    url = "https://cdn-psp.marmot-cloud.com/acwallet/alipayconnectcode?code=golcashier1629873426081sandbox&golSandbox=true&pspName=ALIPAY_CN",
                    method = "GET"
                ),
                activity,
                ApplicationProvider.getApplicationContext(),
                null,
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
        }

        latch.await()
        assertEquals(true, success)
    }

    @Test
    fun onActivityResultTest() {
        val redirectComponentProvider = RedirectComponentProvider()
        assertEquals(false, redirectComponentProvider.get().handleActivityResult(1, 1, null))
    }

    @Test
    fun retrieveSecurityTokenTest() {
        val redirectComponentProvider = RedirectComponentProvider()

        val latch = CountDownLatch(1)
        var device = "11"
        redirectComponentProvider.get().retrieveSecurityToken(
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
