package com.airwallex.android.redirect

import android.app.Activity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.redirect.util.ThemeUtil
import io.mockk.*
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals
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
                    method = null,
                    packageName = null
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
                    method = null,
                    packageName = null
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
        val activity: Activity = mockk(relaxed = true)
        mockkObject(ThemeUtil)

        try {
            redirectComponentProvider.get().handlePaymentIntentResponse(
                "int_hkdmr7v9rg1j58ky8re",
                nextAction = NextAction(
                    type = NextAction.NextActionType.REDIRECT,
                    data = null,
                    dcc = null,
                    url = "https://cdn-psp.marmot-cloud.com/acwallet/alipayconnectcode?code=golcashier1629873426081sandbox&golSandbox=true&pspName=ALIPAY_CN",
                    method = "GET",
                    packageName = null
                ),
                null,
                activity,
                ApplicationProvider.getApplicationContext(),
                null,
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
        } catch (e: Exception) {
        }

        latch.await()
        assertEquals(true, success)
        unmockkAll()
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
            "11",
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
