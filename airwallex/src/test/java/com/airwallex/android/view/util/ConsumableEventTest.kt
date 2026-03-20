package com.airwallex.android.view.util

import org.junit.Assert.*
import org.junit.Test

class ConsumableEventTest {
    @Test
    fun `getContentIfNotHandled does not affect peekContent`() {
        val event = ConsumableEvent(true)
        assertEquals(true, event.peekContent())
        assertEquals(true, event.getContentIfNotHandled())
        assertEquals(true, event.peekContent())
        assertNull(event.getContentIfNotHandled())
        assertEquals(true, event.peekContent())
    }
}