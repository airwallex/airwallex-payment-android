package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentIntent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Interface for providing PaymentIntent objects asynchronously.
 * This allows consumer applications to provide payment intents on demand
 * rather than having to provide them upfront when creating payment sessions.
 *
 * **NO ADDITIONAL INTERFACES REQUIRED**: Consumers only need to implement this interface.
 * The SDK uses an internal repository pattern to manage providers across activity transitions.
 *
 * ## Simple Implementation Example
 * ```kotlin
 * class MyPaymentProvider(private val apiKey: String, private val userId: String?) : PaymentIntentProvider {
 *     override fun provide(callback: PaymentIntentCallback) {
 *         // Your implementation here - any complexity, any properties
 *         // No need to worry about Parcelable or Serializable
 *     }
 * }
 * ```
 *
 * The SDK handles all the complexity of passing providers between activities automatically.
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
 * Internal repository for managing PaymentIntentProvider instances across activity transitions.
 * This uses the same pattern as AirwallexActivityLaunch to store providers in memory
 * and pass only identifiers between activities.
 */
internal object PaymentIntentProviderRepository {
    private val providers = ConcurrentHashMap<String, PaymentIntentProvider>()

    /**
     * Stores a provider and returns its unique identifier.
     */
    fun store(provider: PaymentIntentProvider): String {
        val id = UUID.randomUUID().toString()
        providers[id] = provider
        return id
    }

    /**
     * Retrieves a provider by its identifier.
     */
    fun get(id: String): PaymentIntentProvider? {
        return providers[id]
    }

    /**
     * Removes a provider from storage when no longer needed.
     */
    fun remove(id: String) {
        providers.remove(id)
    }

    /**
     * Clears all stored providers. Used for testing or memory management.
     */
    internal fun clear() {
        providers.clear()
    }
}

/**
 * Extension function to resolve PaymentIntent from session.
 * If paymentIntent is available, calls callback immediately.
 * If paymentIntentProviderId is available, retrieves the provider and uses it to get the intent asynchronously.
 */
fun AirwallexPaymentSession.resolvePaymentIntent(callback: PaymentIntentProvider.PaymentIntentCallback) {
    when {
        paymentIntent != null -> callback.onSuccess(paymentIntent)
        paymentIntentProviderId != null -> {
            val provider = PaymentIntentProviderRepository.get(paymentIntentProviderId)
            if (provider != null) {
                provider.provide(callback)
            } else {
                callback.onError(IllegalStateException("PaymentIntentProvider not found in repository. Provider may have been garbage collected."))
            }
        }
        else -> callback.onError(IllegalStateException("Neither paymentIntent nor paymentIntentProvider available"))
    }
}

/**
 * Extension function to resolve PaymentIntent from recurring session.
 * If paymentIntent is available, calls callback immediately.
 * If paymentIntentProviderId is available, retrieves the provider and uses it to get the intent asynchronously.
 */
fun AirwallexRecurringWithIntentSession.resolvePaymentIntent(callback: PaymentIntentProvider.PaymentIntentCallback) {
    when {
        paymentIntent != null -> callback.onSuccess(paymentIntent)
        paymentIntentProviderId != null -> {
            val provider = PaymentIntentProviderRepository.get(paymentIntentProviderId)
            if (provider != null) {
                provider.provide(callback)
            } else {
                callback.onError(IllegalStateException("PaymentIntentProvider not found in repository. Provider may have been garbage collected."))
            }
        }
        else -> callback.onError(IllegalStateException("Neither paymentIntent nor paymentIntentProvider available"))
    }
}