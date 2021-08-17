package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class NextActionTest {

    @Test
    fun testParcelable() {
        assertEquals(
            NextActionFixtures.NEXTACTION,
            ParcelUtils.createMaybeNull(NextActionFixtures.NEXTACTION)
        )
    }

    @Test
    fun testParams() {
        val nextAction = NextActionFixtures.NEXTACTION!!
        assertEquals(NextAction.NextActionType.RENDER_QR_CODE, nextAction.type)
        assertEquals(null, nextAction.data)
        assertEquals(
            NextAction.DccData(
                currency = "1",
                amount = BigDecimal.valueOf(0.1),
                currencyPair = "1",
                clientRate = 6.44,
                rateSource = "financialMarket",
                rateTimestamp = "1627881115",
                rateExpiry = "1"
            ),
            nextAction.dcc
        )
        assertEquals("https://www.airwallex.com", nextAction.url)
        assertEquals("post", nextAction.method)
    }
}
