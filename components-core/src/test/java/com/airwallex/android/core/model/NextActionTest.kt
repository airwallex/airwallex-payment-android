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
        val dccData = NextAction.DccData(
            currency = "1",
            amount = BigDecimal.valueOf(0.1),
            currencyPair = "1",
            clientRate = 6.44,
            rateSource = "financialMarket",
            rateTimestamp = "1627881115",
            rateExpiry = "1"
        )

        val nextAction = NextActionFixtures.NEXTACTION!!
        assertEquals(NextAction.NextActionType.RENDER_QR_CODE, nextAction.type)
        assertEquals(null, nextAction.data)
        assertEquals(dccData, nextAction.dcc)
        assertEquals("https://www.airwallex.com", nextAction.url)
        assertEquals("post", nextAction.method)
        assertEquals("1", dccData.currency)
        assertEquals(BigDecimal.valueOf(0.1), dccData.amount)
        assertEquals("1", dccData.currencyPair)
        assertEquals(6.44, dccData.clientRate)
        assertEquals("financialMarket", dccData.rateSource)
        assertEquals("1627881115", dccData.rateTimestamp)
        assertEquals("1", dccData.rateExpiry)
    }
}
