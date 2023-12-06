package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.view.inputs.AirwallexTextInputLayout
import com.airwallex.android.view.inputs.CardCvcTextInputLayout
import com.airwallex.android.view.inputs.CardExpiryTextInputLayout
import com.airwallex.android.view.inputs.CardNumberTextInputLayout
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class CardWidgetTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val cardWidget: CardWidget by lazy {
        CardWidget(context, null)
    }

    private val cardNameTextInputLayout by lazy {
        cardWidget.findViewById<AirwallexTextInputLayout>(R.id.atlCardName)
    }

    private val cardNumberTextInputLayout by lazy {
        cardWidget.findViewById<CardNumberTextInputLayout>(R.id.atlCardNumber)
    }

    private val cvcTextInputLayout by lazy {
        cardWidget.findViewById<CardCvcTextInputLayout>(R.id.atlCardCvc)
    }

    private val expiryTextInputLayout by lazy {
        cardWidget.findViewById<CardExpiryTextInputLayout>(R.id.atlCardExpiry)
    }

    @Test
    fun isValidTest() {
        cardNameTextInputLayout.value = "aaa"
        cardNumberTextInputLayout.value = "4242424242424242"
        cvcTextInputLayout.value = "123"
        expiryTextInputLayout.value = "1024"
        assertEquals(true, cardWidget.isValid)

        cardNameTextInputLayout.value = "aaa"
        cardNumberTextInputLayout.value = "4242424242424242"
        cvcTextInputLayout.value = "123"
        expiryTextInputLayout.value = "1019"
        assertEquals(false, cardWidget.isValid)

        cardNameTextInputLayout.value = "aaa"
        cardNumberTextInputLayout.value = "4242424242424243"
        cvcTextInputLayout.value = "123"
        expiryTextInputLayout.value = "1022"
        assertEquals(false, cardWidget.isValid)

        cardNameTextInputLayout.value = "aaa"
        cardNumberTextInputLayout.value = "4242424242424242"
        cvcTextInputLayout.value = "123"
        expiryTextInputLayout.value = "1024"
        assertEquals(
            PaymentMethod.Card.Builder()
                .setName("aaa")
                .setNumber("4242424242424242")
                .setCvc("123")
                .setExpiryMonth("10")
                .setExpiryYear("2024")
                .build(),
            cardWidget.paymentMethodCard
        )
    }

    @Test
    fun cardChangeCallbackTest() {
        val latch = CountDownLatch(1)
        var success = false

        cardWidget.cardChangeCallback = {
            success = true
            latch.countDown()
        }
        cardNumberTextInputLayout.value = "4242424242424242"

        latch.await()
        assertEquals(true, success)
    }
}
