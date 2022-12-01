package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import com.airwallex.android.view.inputs.CardExpiryEditText
import com.airwallex.android.view.inputs.CardExpiryTextInputLayout
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun `test expiry date value`() {
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

    @Test
    fun `test error messages`() {
        assertEquals(
            cardExpiryTextInputLayout.emptyErrorMessage,
            context.getString(R.string.airwallex_empty_expiry)
        )
        assertEquals(
            cardExpiryTextInputLayout.invalidErrorMessage,
            context.getString(R.string.airwallex_invalid_expiry_date)
        )
    }

    @Test
    fun `test isValid`() {
        cardExpiryTextInputLayout.teInput.setText("12/35")
        assertTrue(cardExpiryTextInputLayout.isValid)
    }
}
