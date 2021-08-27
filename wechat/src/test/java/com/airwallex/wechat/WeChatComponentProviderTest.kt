package com.airwallex.wechat

import android.app.Activity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.wechat.WeChatComponentProvider
import com.nhaarman.mockitokotlin2.mock
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
        assertEquals(true, weChatComponentProvider.canHandleAction(PaymentMethodType.WECHAT))
        assertEquals(false, weChatComponentProvider.canHandleAction(PaymentMethodType.CARD))
        assertEquals(false, weChatComponentProvider.canHandleAction(PaymentMethodType.ALIPAY_CN))
    }

    @Test
    fun handlePaymentIntentResponseTest() {
        val weChatComponentProvider = WeChatComponentProvider()

        var success = false
        val activity: Activity = mock()
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
                    "prepayId" to "https://pci-api-demo.airwallex.com/pa/mock/wechat/hk/v2/qr_code_scanned?outTradeNo=2021082506294804770199020854&amount=100",
                    "sign" to "EDD7AFB573F30F4C131898D631AA5ED3DA8FE92289536A6BE43426E71F2A2798",
                    "timeStamp" to 1629872988
                ),
                dcc = null, url = null, method = null
            ),
            activity,
            ApplicationProvider.getApplicationContext(),
            null,
            object : Airwallex.PaymentListener<String> {
                override fun onFailed(exception: AirwallexException) {
                    success = false
                    latch.countDown()
                }

                override fun onCancelled() {
                    success = false
                    latch.countDown()
                }

                override fun onSuccess(response: String) {
                    success = true
                    latch.countDown()
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
        var device = "11"
        weChatComponentProvider.retrieveSecurityToken(
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
