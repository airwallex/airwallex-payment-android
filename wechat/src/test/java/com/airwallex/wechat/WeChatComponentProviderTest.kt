package com.airwallex.wechat

import androidx.appcompat.view.ContextThemeWrapper
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.wechat.WeChatComponentProvider
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals

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
