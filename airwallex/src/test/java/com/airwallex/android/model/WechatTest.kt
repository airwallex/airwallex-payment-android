package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WechatTest {

    @Test
    fun testParcelable() {
        assertEquals(WechatFixtures.WECHAT, ParcelUtils.create(WechatFixtures.WECHAT))
    }
}
