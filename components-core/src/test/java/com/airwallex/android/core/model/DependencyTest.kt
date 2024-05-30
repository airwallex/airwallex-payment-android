package com.airwallex.android.core.model

import kotlin.test.Test
import kotlin.test.assertEquals

class DependencyTest {
    @Test
    fun testMapping() {
        assertEquals("payment-card", Dependency.CARD.value)
        assertEquals("payment-wechat", Dependency.WECHAT.value)
        assertEquals("payment-redirect", Dependency.REDIRECT.value)
        assertEquals("payment-googlepay", Dependency.GOOGLEPAY.value)
    }
}