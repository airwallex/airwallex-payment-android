package com.airwallex.android.redirect

import android.app.Activity
import android.content.Context
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.redirect.exception.RedirectException
import com.airwallex.android.redirect.util.RedirectUtil
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs

class RedirectComponentTest {
    @MockK
    private lateinit var activity: Activity

    @MockK
    private lateinit var context: Context

    @MockK(relaxed = true)
    private lateinit var listener: Airwallex.PaymentResultListener
    private val redirectComponent = RedirectComponent()
    private val testUrl = "http://abc.com"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkObject(RedirectUtil)
        mockkObject(AnalyticsLogger)
    }

    @After
    fun unmock() {
        unmockkAll()
    }

    @Test
    fun testProvider() {
        assertIs<RedirectComponentProvider>(RedirectComponent.PROVIDER)
    }

    @Test
    fun `test handlePaymentIntentResponse success`() {
        every { RedirectUtil.makeRedirect(any(), any()) } just runs

        redirectComponent.handlePaymentIntentResponse(
            "pid",
            NextAction(
                type = NextAction.NextActionType.REDIRECT,
                data = null,
                dcc = null,
                url = testUrl,
                method = null,
                packageName = null
            ),
            null,
            activity,
            context,
            null,
            listener
        )

        verify { AnalyticsLogger.logPageView("payment_redirect", mapOf("url" to testUrl)) }
    }

    @Test
    fun `test handlePaymentIntentResponse fail`() {
        val redirectMsg = "something wrong"
        every { RedirectUtil.makeRedirect(any(), any()) } throws RedirectException(redirectMsg)

        redirectComponent.handlePaymentIntentResponse(
            "pid",
            NextAction(
                type = NextAction.NextActionType.REDIRECT,
                data = null,
                dcc = null,
                url = testUrl,
                method = null,
                packageName = null
            ),
            null,
            activity,
            context,
            null,
            listener
        )

        verify {
            AnalyticsLogger.logError(
                "payment_redirect",
                mapOf("url" to testUrl, "message" to redirectMsg)
            )
        }
    }
}
