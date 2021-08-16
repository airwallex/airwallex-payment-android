package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
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
        expiryTextInputLayout.value = "102023"
        assertEquals(true, cardWidget.isValid)

        cardNameTextInputLayout.value = "aaa"
        cardNumberTextInputLayout.value = "4242424242424242"
        cvcTextInputLayout.value = "123"
        expiryTextInputLayout.value = "102019"
        assertEquals(false, cardWidget.isValid)

        cardNameTextInputLayout.value = "aaa"
        cardNumberTextInputLayout.value = "4242424242424243"
        cvcTextInputLayout.value = "123"
        expiryTextInputLayout.value = "102022"
        assertEquals(false, cardWidget.isValid)
    }
}
