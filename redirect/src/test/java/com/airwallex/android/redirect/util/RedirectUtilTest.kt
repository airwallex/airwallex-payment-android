package com.airwallex.android.redirect.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.redirect.exception.RedirectException
import com.airwallex.android.redirect.util.RedirectUtil.ResolveResultType
import com.airwallex.android.ui.R
import io.mockk.Runs
import io.mockk.andThenJust
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
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

    @Test
    fun `test makeRedirect - success`() {
        val activity = mockk<Activity>(relaxed = true)
        val slot = slot<Intent>()
        val redirectUrl = "http://www.google.com"
        val redirectUri = Uri.parse(redirectUrl)

        every { activity.startActivity(capture(slot)) } just Runs
        RedirectUtil.makeRedirect(activity, redirectUrl)
        assertEquals(Intent.ACTION_VIEW, slot.captured.action)
        assertEquals(redirectUri, slot.captured.data)
    }

    @Test
    fun `test makeRedirect- redirect fails and fallback success`() {
        val activity = mockk<Activity>(relaxed = true)
        val slot = mutableListOf<Intent>()

        val redirectUrl = "http://www.google.com"
        val fallbackUrl = "https://example-fallback.com"
        val fallbackUri = Uri.parse(fallbackUrl)

        every { activity.startActivity(any()) } throws ActivityNotFoundException() andThenJust Runs
        RedirectUtil.makeRedirect(activity, redirectUrl, fallbackUrl)
        verify(exactly = 2) { activity.startActivity(capture(slot)) }
        assertEquals(fallbackUri, slot[1].data)
    }

    @Test(expected = RedirectException::class)
    fun `test makeRedirect - redirect and fallback fail`() {
        val activity = mockk<Activity>(relaxed = true)
        val redirectUrl = "http://www.google.com"
        every { activity.startActivity(any()) } throws ActivityNotFoundException()
        RedirectUtil.makeRedirect(activity, redirectUrl)
    }
}
