package com.airwallex.wechat

import com.airwallex.android.wechat.WeChatComponent
import org.junit.Test
import kotlin.test.assertNotNull

class WeChatComponentTest {

    @Test
    fun test() {
        assertNotNull(WeChatComponent.PROVIDER)
    }
}
