package com.airwallex.android.view.util

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Wrapper for data that is exposed via a Flow representing an event.
 * Ensures the content is consumed only once, even with multiple concurrent collectors.
 */
class ConsumableEvent<out T>(private val content: T) {
    private val hasBeenHandled = AtomicBoolean(false)

    /**
     * Returns the content if it hasn't been handled, and marks it as handled.
     * Thread-safe: only one caller will successfully consume the event.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled.compareAndSet(false, true)) {
            content
        } else {
            null
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}
