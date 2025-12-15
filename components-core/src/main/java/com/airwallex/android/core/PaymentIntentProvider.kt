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
 *
 * Multiple activities can share the same provider ID. The provider is only removed when
 * ALL activities that use it have been destroyed.
 *
 * Each activity can only be bound to ONE provider at a time. If an activity binds to a new
 * provider, it will unbind from the previous one.
 */
internal object PaymentIntentProviderRepository {
    private var isInitialized = false

    // Maps provider IDs to their PaymentIntentProvider instances
    private val providers = ConcurrentHashMap<String, PaymentIntentProvider>()

    // Maps Activity class names to their single provider ID (survives configuration changes)
    // Each activity can only be bound to one provider at a time
    private val activityToProviderMap = ConcurrentHashMap<String, String>()

    // Maps provider IDs to the set of activity class names using this provider
    // When the set becomes empty, the provider is removed
    private val providerToActivitiesMap = ConcurrentHashMap<String, MutableSet<String>>()

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
                    activityToProviderMap[activityKey]?.let { providerId ->
                        // Remove this activity from the provider's activity set
                        providerToActivitiesMap[providerId]?.remove(activityKey)

                        // If no more activities are using this provider, remove it
                        if (providerToActivitiesMap[providerId]?.isEmpty() == true) {
                            providers.remove(providerId)
                            providerToActivitiesMap.remove(providerId)
                        }
                    }
                    activityToProviderMap.remove(activityKey)
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
        providerToActivitiesMap[id] = ConcurrentHashMap.newKeySet()
        return id
    }

    /**
     * Binds a stored provider to an Activity lifecycle.
     * The provider will be automatically cleaned up when ALL activities using it are destroyed.
     * Each activity can only be bound to ONE provider at a time. If the activity was previously
     * bound to a different provider, it will be unbound from the old one first.
     *
     * This can be called multiple times with the same activity class and provider ID
     * (e.g., after configuration changes) - subsequent calls with the same provider ID will be ignored.
     *
     * @param providerId The provider identifier returned from store()
     * @param activity The host Activity that owns this provider
     */
    fun bindToActivity(providerId: String, activity: Activity) {
        if (!providers.containsKey(providerId)) return

        val activityKey = activity.javaClass.name
        val currentProviderId = activityToProviderMap[activityKey]

        // If activity is already bound to this same provider, do nothing
        if (currentProviderId == providerId) return

        // If activity was bound to a different provider, unbind from the old one first
        if (currentProviderId != null) {
            providerToActivitiesMap[currentProviderId]?.remove(activityKey)

            // If no more activities are using the old provider, remove it
            if (providerToActivitiesMap[currentProviderId]?.isEmpty() == true) {
                providers.remove(currentProviderId)
                providerToActivitiesMap.remove(currentProviderId)
            }
        }

        // Bind to the new provider
        activityToProviderMap[activityKey] = providerId
        providerToActivitiesMap[providerId]?.add(activityKey)
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
    val provider = paymentIntentProvider ?: return

    val providerId = paymentIntentProviderId
    if (providerId == null) {
        // Store the provider in the repository and bind to activity
        val newProviderId = PaymentIntentProviderRepository.store(provider)
        PaymentIntentProviderRepository.bindToActivity(newProviderId, activity)
        paymentIntentProviderId = newProviderId
    } else {
        // Already stored, just bind to the new activity
        PaymentIntentProviderRepository.bindToActivity(providerId, activity)
    }
}

/**
 * Extension function to resolve PaymentIntent from session.
 * If paymentIntent is available, calls callback immediately.
 * If paymentIntentProvider is available (transient field), uses it to get the intent asynchronously.
 * If paymentIntentProviderId is available (after binding), retrieves from repository.
 */
fun AirwallexPaymentSession.resolvePaymentIntent(callback: PaymentIntentProvider.PaymentIntentCallback) {
    // Check if we have a static PaymentIntent
    paymentIntent?.let { intent ->
        callback.onSuccess(intent)
        return
    }

    // Check if we have a transient provider (before binding)
    paymentIntentProvider?.let { provider ->
        provider.provide(object : PaymentIntentProvider.PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                paymentIntent.clientSecret?.let { TokenManager.updateClientSecret(it) }
                callback.onSuccess(paymentIntent)
            }

            override fun onError(error: Throwable) {
                callback.onError(error)
            }
        })
        return
    }

    // Check if we have a provider ID (after binding)
    paymentIntentProviderId?.let { providerId ->
        val provider = PaymentIntentProviderRepository.get(providerId)
        if (provider != null) {
            provider.provide(object : PaymentIntentProvider.PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    paymentIntent.clientSecret?.let { TokenManager.updateClientSecret(it) }
                    callback.onSuccess(paymentIntent)
                }

                override fun onError(error: Throwable) {
                    callback.onError(error)
                }
            })
        } else {
            callback.onError(IllegalStateException("PaymentIntentProvider not found in repository. Provider may have been garbage collected."))
        }
        return
    }

    // No payment intent or provider available
    callback.onError(IllegalStateException("Neither paymentIntent nor paymentIntentProvider available"))
}

/**
 * Binds this session's PaymentIntentProvider to an Activity lifecycle.
 * This ensures the provider is cleaned up when the Activity is destroyed.
 * Should be called once when the session starts being used with a specific Activity.
 *
 * @param activity The host Activity that will own this session's provider
 */
fun AirwallexRecurringWithIntentSession.bindToActivity(activity: Activity) {
    val provider = paymentIntentProvider ?: return

    val providerId = paymentIntentProviderId
    if (providerId == null) {
        // Store the provider in the repository and bind to activity
        val newProviderId = PaymentIntentProviderRepository.store(provider)
        PaymentIntentProviderRepository.bindToActivity(newProviderId, activity)
        paymentIntentProviderId = newProviderId
    } else {
        // Already stored, just bind to the new activity
        PaymentIntentProviderRepository.bindToActivity(providerId, activity)
    }
}

/**
 * Extension function to resolve PaymentIntent from recurring session.
 * If paymentIntent is available, calls callback immediately.
 * If paymentIntentProvider is available (transient field), uses it to get the intent asynchronously.
 * If paymentIntentProviderId is available (after binding), retrieves from repository.
 */
fun AirwallexRecurringWithIntentSession.resolvePaymentIntent(callback: PaymentIntentProvider.PaymentIntentCallback) {
    // Check if we have a static PaymentIntent
    paymentIntent?.let { intent ->
        callback.onSuccess(intent)
        return
    }

    // Check if we have a transient provider (before binding)
    paymentIntentProvider?.let { provider ->
        provider.provide(object : PaymentIntentProvider.PaymentIntentCallback {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                paymentIntent.clientSecret?.let { TokenManager.updateClientSecret(it) }
                callback.onSuccess(paymentIntent)
            }

            override fun onError(error: Throwable) {
                callback.onError(error)
            }
        })
        return
    }

    // Check if we have a provider ID (after binding)
    paymentIntentProviderId?.let { providerId ->
        val provider = PaymentIntentProviderRepository.get(providerId)
        if (provider != null) {
            provider.provide(object : PaymentIntentProvider.PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    paymentIntent.clientSecret?.let { TokenManager.updateClientSecret(it) }
                    callback.onSuccess(paymentIntent)
                }

                override fun onError(error: Throwable) {
                    callback.onError(error)
                }
            })
        } else {
            callback.onError(IllegalStateException("PaymentIntentProvider not found in repository. Provider may have been garbage collected."))
        }
        return
    }

    // No payment intent or provider available
    callback.onError(IllegalStateException("Neither paymentIntent nor paymentIntentProvider available"))
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