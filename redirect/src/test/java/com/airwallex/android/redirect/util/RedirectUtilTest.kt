package com.airwallex.android.redirect.util

import android.content.Context
import android.net.Uri
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.redirect.util.RedirectUtil.ResolveResultType
import com.airwallex.android.ui.R
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class RedirectUtilTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    @Test
    fun `test createRedirectIntent - resolve result type UNKNOWN`() {
        val uri = Uri.parse("http://www.google.com")
        val intent = RedirectUtil.createRedirectIntent(context, uri, null)

        assertEquals(
            "http://www.google.com",
            intent.data.toString()
        )
    }

    @Test
    fun `test createRedirectIntent - resolve result type APPLICATION`() {
        mockkObject(RedirectUtil)
        every {
            RedirectUtil["determineResolveResult"](
                any<Context>(),
                any<Uri>(),
                any<String>()
            )
        } returns ResolveResultType.APPLICATION
        val mockContext = mockk<Context>()
        val uri = mockk<Uri>()

        val intent = RedirectUtil.createRedirectIntent(mockContext, uri, "")

        assertEquals(uri, intent.data)
        unmockkAll()
    }
}
