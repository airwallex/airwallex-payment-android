package com.airwallex.android.ui.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.ui.R
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class CardExpiryEditTextTest {
    private lateinit var cardExpiryEditText: CardExpiryEditText

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    @BeforeTest
    fun setup() {
        cardExpiryEditText =
            CardExpiryEditText(context)
        cardExpiryEditText.setText("")
    }

    @Test
    fun expiryDateValueTest() {
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
