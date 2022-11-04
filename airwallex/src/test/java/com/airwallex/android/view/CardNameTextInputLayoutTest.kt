package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class CardNameTextInputLayoutTest {
    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val inputLayout: CardNameTextInputLayout by lazy {
        CardNameTextInputLayout(context, null)
    }

    @Test
    fun `test error messages`() {
        assertEquals(
            inputLayout.emptyErrorMessage,
            context.getString(R.string.airwallex_empty_card_name)
        )
        assertEquals(
            inputLayout.invalidErrorMessage,
            context.getString(R.string.airwallex_empty_card_name)
        )
    }

    @Test
    fun `test isValid`() {
        inputLayout.teInput.setText("Name")
        assertTrue(inputLayout.isValid)
        inputLayout.teInput.setText("")
        assertFalse(inputLayout.isValid)
    }
}