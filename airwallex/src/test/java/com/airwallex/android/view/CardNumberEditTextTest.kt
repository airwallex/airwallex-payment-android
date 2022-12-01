package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.ui.R
import com.airwallex.android.view.inputs.CardNumberEditText
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.*

@RunWith(RobolectricTestRunner::class)
class CardNumberEditTextTest {

    private lateinit var cardNumberEditText: CardNumberEditText
    private val errorMessage = "Error message"
    private var isComplete = false
    private var error: String? = null
    private var cardBrand: CardBrand? = null

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    @BeforeTest
    fun setup() {
        cardNumberEditText = CardNumberEditText(context)
        cardNumberEditText.validationMessageCallback = { cardNumber ->
            if (cardNumber.startsWith("5")) {
                errorMessage
            } else {
                null
            }
        }
        cardNumberEditText.completionCallback = { isComplete = true }
        cardNumberEditText.errorCallback = { error = it }
        cardNumberEditText.brandChangeCallback = { cardBrand = it }

        cardNumberEditText.setText("")
        isComplete = false
        error = null
        cardBrand = null
    }

    @Test
    fun `test mastercard brand and error`() {
        cardNumberEditText.setText("5353 4242 4242 4244")
        assertEquals(cardNumberEditText.validationMessage, errorMessage)
        assertEquals(error, errorMessage)
        assertEquals(cardBrand, CardBrand.MasterCard)
    }

    @Test
    fun `test amex brand and completion when there is error first`() {
        cardNumberEditText.setText("5782 8224 6310 0055")
        assertEquals(error, errorMessage)
        cardNumberEditText.setText("3782 8224 6310 005")
        assertNull(cardNumberEditText.validationMessage)
        assertNull(error)
        assertEquals(cardBrand, CardBrand.Amex)
        assertTrue(isComplete)
    }

    @Test
    fun `test visa brand and error`() {
        cardNumberEditText.setText("4242 4242 4242 4244")
        assertNull(cardNumberEditText.validationMessage)
        assertNull(error)
        assertEquals(cardBrand, CardBrand.Visa)
    }

    @Test
    fun `test amex card number auto formatt`() {
        cardNumberEditText.setText("378282246310005")
        assertEquals(cardNumberEditText.text.toString(), "3782 822463 10005")
    }

    @Test
    fun `test visa card number auto format`() {
        cardNumberEditText.setText("4242424242424244")
        assertEquals(cardNumberEditText.text.toString(), "4242 4242 4242 4244")
    }
}
