package com.airwallex.android.view.util

/**
 * Wrapper for data that is exposed via a Flow representing an event.
 * Ensures the content is consumed only once.
 */
class ConsumableEvent<out T>(private val content: T) {
    private var hasBeenHandled = false

    /**
     * Returns the content if it hasn't been handled, and marks it as handled.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}
