package com.airwallex.android.view

import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.view.util.findWithType
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AvailablePaymentMethodExtensionsTest {

    @Test
    fun `test find with type`() {
        val cardType = mockk<AvailablePaymentMethodType>()
        every { cardType.name } returns PaymentMethodType.CARD.value
        val list = listOf(cardType)

        assertEquals(list.findWithType(PaymentMethodType.CARD), cardType)
        assertNull(list.findWithType(PaymentMethodType.REDIRECT))
    }
}