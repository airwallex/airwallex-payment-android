package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Interface for providing PaymentIntent objects asynchronously.
 * This allows consumer applications to provide payment intents on demand
 * rather than having to provide them upfront when creating payment sessions.
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
 * Modern suspend-based interface for providing PaymentIntent objects.
 * This is the preferred interface for Kotlin consumers as it provides cleaner async handling.
 *
 * ## Kotlin Implementation Example
 * ```kotlin
 * class MyPaymentIntentSource(private val apiService: ApiService) : PaymentIntentSource {
 *     override suspend fun getPaymentIntent(): PaymentIntent {
 *         return apiService.createPaymentIntent()
 *     }
 * }
 * ```
 *
 * ## Java Compatibility
 * If you need Java compatibility, use the callback-based [PaymentIntentProvider] interface instead.
 */
interface PaymentIntentSource {
    /**
     * Retrieves a PaymentIntent using suspend functions.
     * This method should perform any necessary API calls or business logic
     * to create and return a PaymentIntent.
     *
     * @return The PaymentIntent
     * @throws Exception if unable to retrieve the PaymentIntent
     */
    suspend fun getPaymentIntent(): PaymentIntent
}

/**
 * Adapter that bridges [PaymentIntentSource] to [PaymentIntentProvider].
 * This allows modern suspend-based sources to work with callback-based APIs seamlessly.
 */
internal class SourceToProviderAdapter(
    private val source: PaymentIntentSource,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PaymentIntentProvider {

    override fun provide(callback: PaymentIntentProvider.PaymentIntentCallback) {
        scope.launch {
            try {
                val paymentIntent = source.getPaymentIntent()
                callback.onSuccess(paymentIntent)
            } catch (error: Throwable) {
                callback.onError(error)
            }
        }
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