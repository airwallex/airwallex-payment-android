package com.airwallex.paymentacceptance

import com.airwallex.android.core.PaymentIntentSource
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.paymentacceptance.repo.PACheckoutDemoRepository
import com.airwallex.paymentacceptance.repo.ReturnUrl

/**
 * Example implementation of PaymentIntentSource using modern suspend functions.
 * This follows Android architecture patterns where "Source" interfaces handle data retrieval.
 * This is the preferred approach for Kotlin consumers as it provides cleaner async handling
 * without callback boilerplate.
 */
class DemoPaymentIntentSource(
    override val currency: String = Settings.currency,
    override val amount: java.math.BigDecimal = Settings.price.toBigDecimal(),
    private val force3DS: Boolean = Settings.force3DS == "True",
    private val customerId: String? = Settings.cachedCustomerId,
    private val returnUrl: ReturnUrl
) : PaymentIntentSource {
    private val repository = PACheckoutDemoRepository()

    /**
     * Retrieves a PaymentIntent using clean suspend function syntax.
     * No callback boilerplate needed - just return the PaymentIntent or throw an exception.
     */
    override suspend fun getPaymentIntent(): PaymentIntent {
        // This method can use any suspend functions, coroutines, or async operations
        // and the SDK will handle all the callback bridging automatically
        return repository.getPaymentIntentFromServer(
            force3DS = force3DS,
            customerId = customerId,
            returnUrl = returnUrl
        )
    }
}