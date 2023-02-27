package com.airwallex.android.core

import android.os.Build
import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.exception.APIException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.capitalized
import com.airwallex.android.core.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AirwallexPaymentManager(
    private val repository: ApiRepository
) : PaymentManager {

    private val workContext = Dispatchers.Main

    override fun <T> startOperation(options: Options, listener: PaymentListener<T>) {
        CoroutineScope(workContext).launch {
            execute(options, listener)
        }
    }

    override suspend fun retrieveAvailablePaymentMethods(
        options: AirwallexApiRepository.RetrieveAvailablePaymentMethodsOptions
    ): AvailablePaymentMethodTypeResponse {
        val result = runCatching {
            requireNotNull(repository.retrieveAvailablePaymentMethods(options))
        }
        result.fold(
            onSuccess = { return it },
            onFailure = { throw handleError(it) }
        )
    }

    override suspend fun createPaymentMethod(
        options: AirwallexApiRepository.CreatePaymentMethodOptions
    ): PaymentMethod {
        val result = runCatching {
            requireNotNull(repository.createPaymentMethod(options))
        }
        result.fold(
            onSuccess = { return it },
            onFailure = { throw handleError(it) }
        )
    }

    override suspend fun createPaymentConsent(
        options: AirwallexApiRepository.CreatePaymentConsentOptions
    ): PaymentConsent {
        val result = runCatching {
            requireNotNull(repository.createPaymentConsent(options))
        }
        result.fold(
            onSuccess = { return it },
            onFailure = { throw handleError(it) }
        )
    }

    override fun buildDeviceInfo(deviceId: String): Device {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val deviceName = if (model.startsWith(manufacturer)) {
            model.capitalized()
        } else {
            "${manufacturer.capitalized()} ${model.capitalized()}"
        }
        return Device.Builder()
            .setDeviceId(deviceId)
            .setDeviceModel(deviceName)
            .setOsType("Android")
            .setOsVersion(Build.VERSION.RELEASE)
            .build()
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> execute(options: Options, listener: PaymentListener<T>) {
        when (options) {
            is AirwallexApiRepository.RetrievePaymentConsentOptions -> {
                val result = runCatching {
                    requireNotNull(repository.retrievePaymentConsent(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { listener.onSuccess(it as T) },
                        onFailure = { listener.onFailed(handleError(it)) }
                    )
                }
            }
            is AirwallexApiRepository.DisablePaymentConsentOptions -> {
                val result = runCatching {
                    requireNotNull(repository.disablePaymentConsent(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { listener.onSuccess(it as T) },
                        onFailure = { listener.onFailed(handleError(it)) }
                    )
                }
            }
            is AirwallexApiRepository.VerifyPaymentConsentOptions -> {
                val result = runCatching {
                    requireNotNull(repository.verifyPaymentConsent(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { listener.onSuccess(it as T) },
                        onFailure = { listener.onFailed(handleError(it)) }
                    )
                }
            }
            is AirwallexApiRepository.CreatePaymentConsentOptions -> {
                val result = runCatching {
                    requireNotNull(repository.createPaymentConsent(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { listener.onSuccess(it as T) },
                        onFailure = { listener.onFailed(handleError(it)) }
                    )
                }
            }
            is AirwallexApiRepository.RetrieveAvailablePaymentMethodsOptions -> {
                val result = runCatching {
                    requireNotNull(repository.retrieveAvailablePaymentMethods(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { listener.onSuccess(it as T) },
                        onFailure = { listener.onFailed(handleError(it)) }
                    )
                }
            }
            is AirwallexApiRepository.CreatePaymentMethodOptions -> {
                val result = runCatching {
                    requireNotNull(repository.createPaymentMethod(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = {
                            Tracker.track(
                                TrackerRequest.Builder()
                                    .setOrigin(options.request.customerId)
                                    .setCode(TrackerRequest.TrackerCode.ON_PAYMENT_METHOD_CREATED)
                                    .build()
                            )
                            listener.onSuccess(it as T)
                        },
                        onFailure = {
                            Tracker.track(
                                TrackerRequest.Builder()
                                    .setOrigin(options.request.customerId)
                                    .setCode(TrackerRequest.TrackerCode.ON_PAYMENT_METHOD_CREATED_ERROR)
                                    .build()
                            )
                            listener.onFailed(handleError(it))
                        }
                    )
                }
            }
            is AirwallexApiRepository.RetrievePaymentIntentOptions -> {
                val result = runCatching {
                    requireNotNull(repository.retrievePaymentIntent(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = {
                            Tracker.track(
                                TrackerRequest.Builder()
                                    .setIntentId(options.paymentIntentId)
                                    .setCode(TrackerRequest.TrackerCode.ON_INTENT_RETRIEVED)
                                    .build()
                            )
                            listener.onSuccess(it as T)
                        },
                        onFailure = {
                            Tracker.track(
                                TrackerRequest.Builder()
                                    .setIntentId(options.paymentIntentId)
                                    .setCode(TrackerRequest.TrackerCode.ON_INTENT_RETRIEVED_ERROR)
                                    .build()
                            )
                            listener.onFailed(handleError(it))
                        }
                    )
                }
            }
            is AirwallexApiRepository.ConfirmPaymentIntentOptions -> {
                val result = runCatching {
                    requireNotNull(repository.confirmPaymentIntent(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { listener.onSuccess(it as T) },
                        onFailure = { listener.onFailed(handleError(it)) }
                    )
                }
            }
            is AirwallexApiRepository.ContinuePaymentIntentOptions -> {
                val result = runCatching {
                    requireNotNull(repository.continuePaymentIntent(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { listener.onSuccess(it as T) },
                        onFailure = { listener.onFailed(handleError(it)) }
                    )
                }
            }
            is AirwallexApiRepository.RetrievePaymentMethodTypeInfoOptions -> {
                val result = runCatching {
                    requireNotNull(repository.retrievePaymentMethodTypeInfo(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { listener.onSuccess(it as T) },
                        onFailure = { listener.onFailed(handleError(it)) }
                    )
                }
            }
            is AirwallexApiRepository.RetrieveBankOptions -> {
                val result = runCatching {
                    requireNotNull(repository.retrieveBanks(options))
                }
                withContext(Dispatchers.Main) {
                    result.fold(
                        onSuccess = { listener.onSuccess(it as T) },
                        onFailure = { listener.onFailed(handleError(it)) }
                    )
                }
            }
        }
    }

    private fun handleError(throwable: Throwable): AirwallexException {
        return if (throwable is AirwallexException) {
            throwable
        } else {
            APIException(message = throwable.message)
        }
    }
}
