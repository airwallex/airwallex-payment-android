package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.ui.R
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CardExpiryEditTextTest {
    private lateinit var cardExpiryEditText: CardExpiryEditText

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    @Before
    fun setup() {
        cardExpiryEditText =
            CardExpiryEditText(context)
        cardExpiryEditText.setText("")
        mockkObject(ExpiryDateUtils)
    }

    @After
    fun unmock() {
        unmockkAll()
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

    @Test
    fun testErrorCallbackWhenCardExpires() {
        every { ExpiryDateUtils.isExpiryDateValid(any(), any()) } returns false
        var hasError = false
        cardExpiryEditText.errorCallback = { hasError = it }
        cardExpiryEditText.setText("12/21")
        assertTrue(hasError)
    }

    @Test
    fun testErrorCallbackWhenYearLengthIsNotTwo() {
        var hasError = false
        cardExpiryEditText.errorCallback = { hasError = it }
        cardExpiryEditText.setText("12/2023")
        assertTrue(hasError)
    }

    @Test
    fun testValidDateFields() {
        cardExpiryEditText.setText("12/23")
        assertEquals(cardExpiryEditText.validDateFields, Pair(12, 2023))
    }
}
