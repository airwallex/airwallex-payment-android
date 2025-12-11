package com.airwallex.android.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.airwallex.android.core.model.PaymentIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
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
     * Amount currency. Required for payment session creation.
     */
    val currency: String

    /**
     * Payment amount. This is the order amount you would like to charge your customer. Required for payment session creation.
     */
    val amount: BigDecimal

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
     * Amount currency. Required for payment session creation.
     */
    val currency: String

    /**
     * Payment amount. This is the order amount you would like to charge your customer. Required for payment session creation.
     */
    val amount: BigDecimal

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

    override val currency: String
        get() = source.currency

    override val amount: BigDecimal
        get() = source.amount

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
 * Internal repository for managing PaymentIntentProvider instances scoped to Activity lifecycle.
 * This uses the same pattern as AirwallexActivityLaunch to store providers in memory
 * and pass only identifiers between activities. Providers are automatically cleaned up
 * when the host Activity is destroyed.
 */
internal object PaymentIntentProviderRepository {
    private var isInitialized = false

    // Maps provider IDs to their PaymentIntentProvider instances
    private val providers = ConcurrentHashMap<String, PaymentIntentProvider>()

    // Maps Activity class names to their provider IDs for cleanup (survives configuration changes)
    private val activityProvidersMap = ConcurrentHashMap<String, MutableSet<String>>()

    /**
     * Initializes the repository with Activity lifecycle callbacks.
     * Should be called during Application initialization.
     */
    fun initialize(application: Application) {
        if (isInitialized) return
        isInitialized = true

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                // Only clean up providers if the Activity is finishing (not just configuration change)
                if (activity.isFinishing) {
                    val activityKey = activity.javaClass.name
                    activityProvidersMap[activityKey]?.forEach { providerId ->
                        providers.remove(providerId)
                    }
                    activityProvidersMap.remove(activityKey)
                }
            }
        })
    }

    /**
     * Stores a provider and returns its unique identifier.
     * The provider is stored globally first, then should be bound to an Activity
     * using bindToActivity() when the host Activity is known.
     *
     * @param provider The PaymentIntentProvider to store
     * @return Unique identifier for the stored provider
     */
    fun store(provider: PaymentIntentProvider): String {
        val id = UUID.randomUUID().toString()
        providers[id] = provider
        return id
    }

    /**
     * Binds a stored provider to an Activity lifecycle.
     * The provider will be automatically cleaned up when the Activity is destroyed.
     * This should be called when the session is used with a known Activity.
     * Can be called multiple times with different Activity instances of the same class
     * (e.g., after configuration changes) - the binding will persist.
     *
     * @param providerId The provider identifier returned from store()
     * @param activity The host Activity that owns this provider
     */
    fun bindToActivity(providerId: String, activity: Activity) {
        if (providers.containsKey(providerId)) {
            val activityKey = activity.javaClass.name
            activityProvidersMap.getOrPut(activityKey) { ConcurrentHashMap.newKeySet() }.add(providerId)
        }
    }

    /**
     * Retrieves a provider by its identifier.
     */
    fun get(id: String): PaymentIntentProvider? {
        return providers[id]
    }
}

/**
 * Binds this session's PaymentIntentProvider to an Activity lifecycle.
 * This ensures the provider is cleaned up when the Activity is destroyed.
 * Should be called once when the session starts being used with a specific Activity.
 *
 * @param activity The host Activity that will own this session's provider
 */
fun AirwallexPaymentSession.bindToActivity(activity: Activity) {
    paymentIntentProviderId?.let { providerId ->
        PaymentIntentProviderRepository.bindToActivity(providerId, activity)
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
 * Binds this session's PaymentIntentProvider to an Activity lifecycle.
 * This ensures the provider is cleaned up when the Activity is destroyed.
 * Should be called once when the session starts being used with a specific Activity.
 *
 * @param activity The host Activity that will own this session's provider
 */
fun AirwallexRecurringWithIntentSession.bindToActivity(activity: Activity) {
    paymentIntentProviderId?.let { providerId ->
        PaymentIntentProviderRepository.bindToActivity(providerId, activity)
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

/**
 * Binds an AirwallexSession's PaymentIntentProvider to an Activity lifecycle.
 * This is a convenience function that handles all session types.
 *
 * @param activity The host Activity that will own this session's provider
 */
fun AirwallexSession.bindToActivity(activity: Activity) {
    when (this) {
        is AirwallexPaymentSession -> this.bindToActivity(activity)
        is AirwallexRecurringWithIntentSession -> this.bindToActivity(activity)
        // AirwallexRecurringSession doesn't use PaymentIntentProvider
    }
}