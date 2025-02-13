package com.airwallex.wechat

import android.app.Activity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.wechat.WeChatComponentProvider
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class WeChatComponentProviderTest {

    private val context = ContextThemeWrapper()

    @Test
    fun canHandleActionTest() {
        val weChatComponentProvider = WeChatComponentProvider()
        assertEquals(
            true,
            weChatComponentProvider.canHandleAction(
                NextAction(
                    type = NextAction.NextActionType.CALL_SDK,
                    data = null,
                    dcc = null,
                    url = null,
                    method = null,
                    fallbackUrl = null,
                    packageName = null
                )
            )
        )
        assertEquals(
            false,
            weChatComponentProvider.canHandleAction(
                NextAction(
                    type = NextAction.NextActionType.REDIRECT,
                    data = null,
                    dcc = null,
                    url = null,
                    method = null,
                    fallbackUrl = null,
                    packageName = null
                )
            )
        )
        assertEquals(
            false, weChatComponentProvider.canHandleAction(null)
        )
    }

    @Test
    fun handlePaymentIntentResponseTest() {
        val weChatComponentProvider = WeChatComponentProvider()

        var success = false
        val activity: Activity = mockk()
        val latch = CountDownLatch(1)
        weChatComponentProvider.get().handlePaymentIntentResponse(
            "int_hkdmr7v9rg1j58ky8re",
            nextAction = NextAction(
                type = NextAction.NextActionType.CALL_SDK,
                data = mapOf(
                    "appId" to "wx4c86d73fe4f82431",
                    "nonceStr" to "DUY8tIYUmyKO6Lhb1jTBFKUBWNud6XXu",
                    "package" to "Sign=WXPay",
                    "partnerId" to 403011682,
                    "prepayId" to "https://api-demo.airwallex.com/pa/mock/wechat/hk/v2/qr_code_scanned?outTradeNo=2021082506294804770199020854&amount=100",
                    "sign" to "EDD7AFB573F30F4C131898D631AA5ED3DA8FE92289536A6BE43426E71F2A2798",
                    "timeStamp" to 1629872988
                ),
                dcc = null, url = null, method = null, packageName = null, fallbackUrl = null,
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

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(false, success)
    }

    @Test
    fun onActivityResultTest() {
        val weChatComponentProvider = WeChatComponentProvider()
        assertEquals(false, weChatComponentProvider.get().handleActivityResult(1, 1, null))
    }

    @Test
    fun retrieveSecurityTokenTest() {
        val weChatComponentProvider = WeChatComponentProvider()

        val latch = CountDownLatch(1)
        var device = ""
        weChatComponentProvider.get().retrieveSecurityToken(
            "session_id",
            object :
                SecurityTokenListener {
                override fun onResponse(deviceId: String) {
                    device = deviceId
                    latch.countDown()
                }
            }
        )
        latch.await()
        assertEquals("session_id", device)
    }
}
