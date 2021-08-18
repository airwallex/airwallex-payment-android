package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class WeChatTest {

    val weChat = WeChat(
        appId = "wx4c86d73fe4f82431",
        partnerId = "353449704",
        prepayId = "wx271505457540563dd4caf8f393a5f90000",
        `package` = "Sign=WXPay",
        nonceStr = "9v4q5sQpv15Cd5um8ZUvH5pWWuoTW31m",
        timestamp = "1622099145",
        sign = "726102F864EBE0A5465776710EE54AD9D33C051B71A232A6984EDF268C330C83"
    )

    @Test
    fun testParams() {
        assertEquals("wx4c86d73fe4f82431", weChat.appId)
        assertEquals("353449704", weChat.partnerId)
        assertEquals("wx271505457540563dd4caf8f393a5f90000", weChat.prepayId)
        assertEquals("Sign=WXPay", weChat.`package`)
        assertEquals("9v4q5sQpv15Cd5um8ZUvH5pWWuoTW31m", weChat.nonceStr)
        assertEquals("1622099145", weChat.timestamp)
        assertEquals(
            "726102F864EBE0A5465776710EE54AD9D33C051B71A232A6984EDF268C330C83",
            weChat.sign
        )
    }
}
