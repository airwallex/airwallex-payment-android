package com.airwallex.android.redirect

import androidx.appcompat.view.ContextThemeWrapper
import com.airwallex.android.core.SecurityTokenListener
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
