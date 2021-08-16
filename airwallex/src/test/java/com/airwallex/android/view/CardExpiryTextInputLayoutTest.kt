package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class CardExpiryTextInputLayoutTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val cardExpiryTextInputLayout: CardExpiryTextInputLayout by lazy {
        CardExpiryTextInputLayout(context, null)
    }

    @Test
    fun expiryDateValueTest() {
        val cardExpiryEditText =
            cardExpiryTextInputLayout.findViewById<CardExpiryEditText>(R.id.teInput)
        cardExpiryEditText.setText("")
        cardExpiryEditText.append("1")
        cardExpiryEditText.append("1")
        val text = cardExpiryEditText.text.toString()
        assertEquals("11/", text)

        cardExpiryEditText.setText("")
        cardExpiryEditText.append("4")
        assertEquals("04/", cardExpiryEditText.text.toString())

        cardExpiryEditText.setText("")
        cardExpiryEditText.append("1")
        assertEquals("1", cardExpiryEditText.text.toString())

        cardExpiryEditText.setText("")
        cardExpiryEditText.append("1")
        cardExpiryEditText.append("2")
        cardExpiryEditText.append("3")
        assertEquals("12/3", cardExpiryEditText.text.toString())
    }
}
