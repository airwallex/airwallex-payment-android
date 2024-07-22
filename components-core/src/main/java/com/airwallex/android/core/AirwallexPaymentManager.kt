package com.airwallex.android.core

import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.exception.APIException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.capitalized
import com.airwallex.android.core.extension.splitByUppercaseWithSeparator
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Device
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.getUrl
import com.airwallex.android.core.util.BuildHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AirwallexPaymentManager(
    private val repository: ApiRepository
) : PaymentManager {

    private val workContext = Dispatchers.Main

    override fun <T> startOperation(options: Options, listener: PaymentListener<T>) {
        CoroutineScope(workContext).launch {
            execute(options, listener)
        }
    }

    override suspend fun retrieveAvailablePaymentConsents(
        options: Options.RetrieveAvailablePaymentConsentsOptions
    ): Page<PaymentConsent> {
        val result = runCatching {
            requireNotNull(repository.retrieveAvailablePaymentConsents(options))
        }
        result.fold(
            onSuccess = { return it },
            onFailure = { throw handleError(it) }
        )
    }

    override suspend fun retrieveAvailablePaymentMethods(
        options: Options.RetrieveAvailablePaymentMethodsOptions
    ): Page<AvailablePaymentMethodType> {
        val result = runCatching {
            requireNotNull(repository.retrieveAvailablePaymentMethods(options))
        }
        result.fold(
            onSuccess = { return it },
            onFailure = { throw handleError(it) }
        )
    }

    override suspend fun createPaymentMethod(
        options: Options.CreatePaymentMethodOptions
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
        options: Options.CreatePaymentConsentOptions
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
        val manufacturer = BuildHelper.manufacturer
        val model = BuildHelper.model
        val deviceName = if (model.startsWith(manufacturer)) {
            model.capitalized()
        } else {
            "${manufacturer.capitalized()} ${model.capitalized()}"
        }
        return Device.Builder()
            .setDeviceId(deviceId)
            .setDeviceModel(deviceName)
            .setOsType("Android")
            .setOsVersion(BuildHelper.versionRelease)
            .build()
    }

    @Suppress("UNCHECKED_CAST", "ComplexMethod", "LongMethod")
    private suspend fun <T> execute(options: Options, listener: PaymentListener<T>) {
        val result = runCatching {
            when (options) {
                is Options.RetrievePaymentConsentOptions ->
                    requireNotNull(repository.retrievePaymentConsent(options))
                is Options.DisablePaymentConsentOptions ->
                    requireNotNull(repository.disablePaymentConsent(options))
                is Options.VerifyPaymentConsentOptions ->
                    requireNotNull(repository.verifyPaymentConsent(options))
                is Options.CreatePaymentConsentOptions ->
                    requireNotNull(repository.createPaymentConsent(options))
                is Options.RetrieveAvailablePaymentConsentsOptions ->
                    requireNotNull(repository.retrieveAvailablePaymentConsents(options))
                is Options.RetrieveAvailablePaymentMethodsOptions ->
                    requireNotNull(repository.retrieveAvailablePaymentMethods(options))
                is Options.CreatePaymentMethodOptions ->
                    requireNotNull(repository.createPaymentMethod(options))
                is Options.RetrievePaymentIntentOptions ->
                    requireNotNull(repository.retrievePaymentIntent(options))
                is Options.ConfirmPaymentIntentOptions ->
                    requireNotNull(repository.confirmPaymentIntent(options))
                is Options.ContinuePaymentIntentOptions ->
                    requireNotNull(repository.continuePaymentIntent(options))
                is Options.RetrievePaymentMethodTypeInfoOptions ->
                    requireNotNull(repository.retrievePaymentMethodTypeInfo(options))
                is Options.RetrieveBankOptions ->
                    requireNotNull(repository.retrieveBanks(options))
            }
        }

        result.fold(
            onSuccess = {
                listener.onSuccess(it as T)
            },
            onFailure = {
                val exception = handleError(it)
                AnalyticsLogger.logError(
                    eventName = options.getEventName(),
                    url = options.getUrl(),
                    exception = exception
                )
                listener.onFailed(exception)
            }
        )
    }

    private fun handleError(throwable: Throwable): AirwallexException {
        return if (throwable is AirwallexException) {
            throwable
        } else {
            APIException(message = throwable.message)
        }
    }

    private fun Options.getEventName(): String {
        return this::class.java.simpleName.replace("Options", "")
            .splitByUppercaseWithSeparator("_").lowercase()
    }
}
