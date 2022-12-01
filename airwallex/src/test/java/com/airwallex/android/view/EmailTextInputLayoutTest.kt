package com.airwallex.android.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.R
import com.airwallex.android.view.inputs.EmailTextInputLayout
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class EmailTextInputLayoutTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    private val inputLayout: EmailTextInputLayout by lazy {
        EmailTextInputLayout(context, null)
    }

    @Test
    fun `test error messages`() {
        assertEquals(
            inputLayout.emptyErrorMessage,
            context.getString(R.string.airwallex_empty_email)
        )
        assertEquals(
            inputLayout.invalidErrorMessage,
            context.getString(R.string.airwallex_invalid_email)
        )
    }

    @Test
    fun `test isValid`() {
        inputLayout.teInput.setText("john.doe@airwallex.com")
        assertTrue(inputLayout.isValid)
        inputLayout.teInput.setText("abc@de")
        assertFalse(inputLayout.isValid)
    }
}
