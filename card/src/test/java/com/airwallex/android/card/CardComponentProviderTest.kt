package com.airwallex.android.card

import android.app.Activity
import com.airwallex.android.core.*
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.parser.AvailablePaymentMethodTypeParser
import com.airwallex.android.core.model.parser.PageParser
import org.junit.Test
import kotlin.test.assertEquals
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import kotlin.test.assertTrue

@Suppress("NotNullAssertionOperatorRule")
class CardComponentProviderTest {
    private val mockResponse: Page<AvailablePaymentMethodType> =
        PageParser(AvailablePaymentMethodTypeParser()).parse(
            JSONObject(
                """
                    {
                    "items":[
                    {
                   "name":"card",
                   "card_schemes":[{ "name": "mastercard" }]
                    }   
                    ],
                    "has_more":false
                    }
                """.trimIndent()
            )
        )

    @Test
    fun onActivityResultTest() {
        val cardComponentProvider = CardComponentProvider()
        assertEquals(false, cardComponentProvider.get().handleActivityResult(1, 1, null))
    }

    @Test
    fun canHandleActionTest() {
        val cardComponentProvider = CardComponentProvider()
        assertEquals(
            true,
            cardComponentProvider.canHandleAction(
                NextAction(
                    stage = NextAction.NextActionStage.WAITING_USER_INFO_INPUT,
                    type = NextAction.NextActionType.DCC,
                    data = null,
                    dcc = null,
                    url = null,
                    method = null,
                    fallbackUrl = null,
                    packageName = null
                )
            )
        )
        assertEquals(
            true,
            cardComponentProvider.canHandleAction(
                NextAction(
                    stage = NextAction.NextActionStage.WAITING_USER_INFO_INPUT,
                    type = NextAction.NextActionType.REDIRECT,
                    data = mapOf("1" to "2"),
                    dcc = null,
                    url = null,
                    method = null,
                    fallbackUrl = null,
                    packageName = null
                )
            )
        )
        assertEquals(
            false,
            cardComponentProvider.canHandleAction(
                NextAction(
                    stage = NextAction.NextActionStage.WAITING_USER_INFO_INPUT,
                    type = NextAction.NextActionType.CALL_SDK,
                    data = null,
                    dcc = null,
                    url = null,
                    method = null,
                    fallbackUrl = null,
                    packageName = null
                )
            )
        )
    }

    @Test
    fun canHandleSessionAndPaymentMethodTest() = runTest {
        val activity = mockk<Activity>()
        val session = mockk<AirwallexSession>()
        val paymentMethodType = mockResponse.items.first()
        val cardComponentProvider = CardComponentProvider()
        assertTrue(
            cardComponentProvider.canHandleSessionAndPaymentMethod(
                session,
                paymentMethodType,
                activity
            )
        )
    }
}
