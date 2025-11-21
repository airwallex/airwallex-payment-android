package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentIntent

/**
 * Interface for providing PaymentIntent objects asynchronously.
 * This allows consumer applications to provide payment intents on demand
 * rather than having to provide them upfront when creating payment sessions.
 */
interface PaymentIntentProvider {

    /**
     * Provides a PaymentIntent asynchronously.
     *
     * @param callback Callback to receive the PaymentIntent result
     */
    fun provide(callback: PaymentIntentCallback)

    /**
     * Callback interface for receiving PaymentIntent results
     */
    interface PaymentIntentCallback {
        /**
         * Called when PaymentIntent is successfully provided
         *
         * @param paymentIntent The successfully provided PaymentIntent
         */
        fun onSuccess(paymentIntent: PaymentIntent)

        /**
         * Called when there's an error providing the PaymentIntent
         *
         * @param error The error that occurred
         */
        fun onError(error: Throwable)
    }
}

/**
 * Extension function to resolve PaymentIntent from session.
 * If paymentIntent is available, calls callback immediately.
 * If only paymentIntentProvider is available, uses it to get the intent asynchronously.
 */
fun AirwallexPaymentSession.resolvePaymentIntent(callback: PaymentIntentProvider.PaymentIntentCallback) {
    when {
        paymentIntent != null -> callback.onSuccess(paymentIntent)
        paymentIntentProvider != null -> paymentIntentProvider.provide(callback)
        else -> callback.onError(IllegalStateException("Neither paymentIntent nor paymentIntentProvider available"))
    }
}

/**
 * Extension function to resolve PaymentIntent from recurring session.
 * If paymentIntent is available, calls callback immediately.
 * If only paymentIntentProvider is available, uses it to get the intent asynchronously.
 */
fun AirwallexRecurringWithIntentSession.resolvePaymentIntent(callback: PaymentIntentProvider.PaymentIntentCallback) {
    when {
        paymentIntent != null -> callback.onSuccess(paymentIntent)
        paymentIntentProvider != null -> paymentIntentProvider.provide(callback)
        else -> callback.onError(IllegalStateException("Neither paymentIntent nor paymentIntentProvider available"))
    }
}