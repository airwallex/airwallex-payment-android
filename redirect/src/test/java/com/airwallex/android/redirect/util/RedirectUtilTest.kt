package com.airwallex.android.redirect.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.view.ContextThemeWrapper
import androidx.browser.customtabs.CustomTabsIntent
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.RedirectMode
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
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class RedirectUtilTest {

    private val context = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.AirwallexDefaultTheme
    )

    @After
    fun tearDown() {
        unmockkAll()
    }

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

    @Test
    fun `test createRedirectIntent - EXTERNAL_BROWSER mode returns plain intent`() {
        mockkObject(AirwallexPlugins)
        every { AirwallexPlugins.redirectMode } returns RedirectMode.EXTERNAL_BROWSER

        val uri = Uri.parse("http://www.google.com")
        val intent = RedirectUtil.createRedirectIntent(context, uri, null)

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(uri, intent.data)
        // External browser intent should not have CustomTabsIntent extras
        assertTrue(intent.extras?.containsKey(CustomTabsIntent.EXTRA_SESSION) != true)
    }

    @Test
    fun `test createRedirectIntent - CUSTOM_TAB mode returns CustomTabsIntent`() {
        mockkObject(AirwallexPlugins)
        every { AirwallexPlugins.redirectMode } returns RedirectMode.CUSTOM_TAB

        val uri = Uri.parse("http://www.google.com")
        val intent = RedirectUtil.createRedirectIntent(context, uri, null)

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(uri, intent.data)
        // CustomTabsIntent should have session extras
        assertNotNull(intent.extras)
        assertTrue(intent.extras?.containsKey(CustomTabsIntent.EXTRA_SESSION) == true)
    }

    @Test
    fun `test createRedirectIntent - CUSTOM_TAB_BOTTOM_SHEET mode sets initial height`() {
        mockkObject(AirwallexPlugins)
        every { AirwallexPlugins.redirectMode } returns RedirectMode.CUSTOM_TAB_BOTTOM_SHEET

        val uri = Uri.parse("http://www.google.com")
        val intent = RedirectUtil.createRedirectIntent(context, uri, null)

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(uri, intent.data)
        assertNotNull(intent.extras)
        // Bottom sheet mode should have initial height set
        assertTrue(intent.extras?.containsKey(CustomTabsIntent.EXTRA_INITIAL_ACTIVITY_HEIGHT_PX) == true)
    }

    @Suppress("DEPRECATION")
    @Test
    fun `test makeRedirect - CUSTOM_TAB_BOTTOM_SHEET uses startActivityForResult`() {
        mockkObject(AirwallexPlugins)
        every { AirwallexPlugins.redirectMode } returns RedirectMode.CUSTOM_TAB_BOTTOM_SHEET

        val activity = mockk<Activity>(relaxed = true)
        val redirectUrl = "http://www.google.com"

        // Mock resources and display metrics for bottom sheet height calculation
        val resources = context.resources
        every { activity.resources } returns resources
        every { activity.packageManager } returns context.packageManager
        every { activity.startActivityForResult(any(), any()) } just Runs

        RedirectUtil.makeRedirect(activity, redirectUrl)

        // Should use startActivityForResult for bottom sheet mode
        verify { activity.startActivityForResult(any(), any()) }
    }

    @Test
    fun `test makeRedirect - EXTERNAL_BROWSER uses startActivity`() {
        mockkObject(AirwallexPlugins)
        every { AirwallexPlugins.redirectMode } returns RedirectMode.EXTERNAL_BROWSER

        val activity = mockk<Activity>(relaxed = true)
        val redirectUrl = "http://www.google.com"

        every { activity.startActivity(any()) } just Runs

        RedirectUtil.makeRedirect(activity, redirectUrl)

        // Should use startActivity for external browser mode
        verify { activity.startActivity(any()) }
        verify(exactly = 0) { activity.startActivityForResult(any(), any()) }
    }

    @Test
    fun `test makeRedirect - CUSTOM_TAB uses startActivity`() {
        mockkObject(AirwallexPlugins)
        every { AirwallexPlugins.redirectMode } returns RedirectMode.CUSTOM_TAB

        val activity = mockk<Activity>(relaxed = true)
        val redirectUrl = "http://www.google.com"

        every { activity.startActivity(any()) } just Runs

        RedirectUtil.makeRedirect(activity, redirectUrl)

        // Should use startActivity for custom tab mode
        verify { activity.startActivity(any()) }
        verify(exactly = 0) { activity.startActivityForResult(any(), any()) }
    }
}
