package com.airwallex.android.card

import com.airwallex.android.core.model.PaymentMethodType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class CardComponentProviderTest {

    @Test
    fun canHandleActionTest() {
        val cardComponentProvider = CardComponentProvider()
        assertEquals(true, cardComponentProvider.canHandleAction(PaymentMethodType.CARD))
        assertEquals(false, cardComponentProvider.canHandleAction(PaymentMethodType.ALIPAY_CN))
        assertEquals(false, cardComponentProvider.canHandleAction(PaymentMethodType.WECHAT))
    }
}
