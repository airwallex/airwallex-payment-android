package com.airwallex.android.core

import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.exception.APIException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AirwallexPaymentManager(
    private val repository: ApiRepository
) : PaymentManager {

    private fun handleError(throwable: Throwable): AirwallexException {
        return if (throwable is AirwallexException) {
            throwable
        } else {
            APIException(message = throwable.message)
        }
    }

    /**
     * Continue the [PaymentIntent] using [Options], used for 3DS
     *
     * @param options contains the confirm [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun continuePaymentIntent(
        options: Options,
        listener: PaymentListener<PaymentIntent>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.continuePaymentIntent(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
    }

    /**
     * Confirm the [PaymentIntent] using [Options]
     *
     * @param options contains the confirm [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun confirmPaymentIntent(
        options: Options,
        listener: PaymentListener<PaymentIntent>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.confirmPaymentIntent(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
    }

    /**
     * Retrieve the [PaymentIntent] using [Options]
     *
     * @param options contains the retrieve [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun retrievePaymentIntent(
        options: Options,
        listener: PaymentListener<PaymentIntent>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.retrievePaymentIntent(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = {
                        Tracker.track(
                            TrackerRequest.Builder()
                                .setIntentId((options as AirwallexApiRepository.RetrievePaymentIntentOptions).paymentIntentId)
                                .setCode(TrackerRequest.TrackerCode.ON_INTENT_RETRIEVED)
                                .build()
                        )
                        listener.onSuccess(it)
                    },
                    onFailure = {
                        Tracker.track(
                            TrackerRequest.Builder()
                                .setIntentId((options as AirwallexApiRepository.RetrievePaymentIntentOptions).paymentIntentId)
                                .setCode(TrackerRequest.TrackerCode.ON_INTENT_RETRIEVED_ERROR)
                                .setError(it.localizedMessage)
                                .build()
                        )
                        listener.onFailed(handleError(it))
                    }
                )
            }
        }
    }

    /**
     * Retrieve paRes with id
     *
     * @param options contains the retrieve [ThreeDSecurePares] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun retrieveParesWithId(options: Options, listener: PaymentListener<ThreeDSecurePares>) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.retrieveParesWithId(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
    }

    /**
     * Create a [PaymentMethod] using [Options]
     *
     * @param options contains the create [PaymentMethod] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun createPaymentMethod(
        options: Options,
        listener: PaymentListener<PaymentMethod>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.createPaymentMethod(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = {
                        Tracker.track(
                            TrackerRequest.Builder()
                                .setOrigin((options as AirwallexApiRepository.CreatePaymentMethodOptions).request.customerId)
                                .setCode(TrackerRequest.TrackerCode.ON_PAYMENT_METHOD_CREATED)
                                .build()
                        )
                        listener.onSuccess(it)
                    },
                    onFailure = {
                        Tracker.track(
                            TrackerRequest.Builder()
                                .setOrigin((options as AirwallexApiRepository.CreatePaymentMethodOptions).request.customerId)
                                .setCode(TrackerRequest.TrackerCode.ON_PAYMENT_METHOD_CREATED_ERROR)
                                .setError(it.localizedMessage)
                                .build()
                        )
                        listener.onFailed(handleError(it))
                    }
                )
            }
        }
    }

    /**
     * Retrieve all of the customer's [AvailablePaymentMethodResponse] using [Options]
     *
     * @param options contains the retrieve [AvailablePaymentMethodResponse] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun retrieveAvailablePaymentMethods(options: Options, listener: PaymentListener<AvailablePaymentMethodResponse>) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.retrieveAvailablePaymentMethods(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
    }

    /**
     * Create a [PaymentConsent] using [Options]
     *
     * @param options contains the create [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun createPaymentConsent(options: Options, listener: PaymentListener<PaymentConsent>) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.createPaymentConsent(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
    }

    /**
     * Verify a [PaymentConsent] using [Options]
     *
     * @param options contains the verify [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun verifyPaymentConsent(options: Options, listener: PaymentListener<PaymentConsent>) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.verifyPaymentConsent(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
    }

    /**
     * Disable a [PaymentConsent] using [Options]
     *
     * @param options contains the disable [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun disablePaymentConsent(options: Options, listener: PaymentListener<PaymentConsent>) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.disablePaymentConsent(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
    }

    /**
     * Retrieve a [PaymentConsent] using [Options]
     *
     * @param options contains the retrieve [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    override fun retrievePaymentConsent(options: Options, listener: PaymentListener<PaymentConsent>) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching {
                requireNotNull(repository.retrievePaymentConsent(options))
            }
            withContext(Dispatchers.Main) {
                result.fold(
                    onSuccess = { listener.onSuccess(it) },
                    onFailure = { listener.onFailed(handleError(it)) }
                )
            }
        }
    }
}
