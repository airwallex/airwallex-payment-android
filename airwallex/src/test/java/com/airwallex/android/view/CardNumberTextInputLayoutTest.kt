package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CardNumberTextInputLayoutTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val cardNumberTextInputLayout: CardNumberTextInputLayout by lazy {
        CardNumberTextInputLayout(context, null)
    }

    @Test
    fun cardNumberValueTest() {
        val cardNumberEditText = cardNumberTextInputLayout.findViewById<CardNumberEditText>(R.id.teInput)
        cardNumberEditText.setText("")
        cardNumberEditText.setText("4242 4242 4242 4242")
        assertTrue(cardNumberEditText.isCardNumberValid)

        cardNumberEditText.setText("4242424242424242")
        assertTrue(cardNumberEditText.isCardNumberValid)
    }

    @Test
    fun cardNumberFormatTest() {
        val cardNumberEditText = cardNumberTextInputLayout.findViewById<CardNumberEditText>(R.id.teInput)
        cardNumberEditText.setText("")
        cardNumberEditText.setText("4242 4242 4242 4242")
        assertEquals("4242424242424242", cardNumberEditText.cardNumber)
    }

    @Test
    fun completionCallbackTest() {
        val latch = CountDownLatch(1)
        var success = false

        cardNumberTextInputLayout.completionCallback = {
            success = true
            latch.countDown()
        }
        cardNumberTextInputLayout.value = "4242424242424242"

        latch.await()
        assertEquals(true, success)
    }
}
