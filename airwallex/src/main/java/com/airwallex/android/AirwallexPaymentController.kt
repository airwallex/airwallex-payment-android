package com.airwallex.android

import com.airwallex.android.exception.APIException
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal class AirwallexPaymentController(
    private val repository: ApiRepository,
    private val workScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PaymentController {

    override fun confirmPaymentIntent(
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentParams,
        callback: Airwallex.PaymentCallback<PaymentIntent>
    ) {
        ConfirmIntentTask(
            options,
            paymentIntentParams,
            repository,
            workScope,
            object : ApiResultCallback<AirwallexHttpResponse> {
                override fun onSuccess(result: AirwallexHttpResponse) {
                    if (result.isSuccessful && result.body != null) {
                        val paymentIntent = AirwallexPlugins.gson.fromJson(
                            result.body.string(),
                            PaymentIntent::class.java
                        )
                        callback.onSuccess(paymentIntent)
                    } else {
                        callback.onFailed(handleAPIError(result))
                    }
                }

                override fun onError(e: AirwallexException) {
                    callback.onFailed(e)
                }
            }
        ).execute()
    }

    override fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentCallback<PaymentIntent>
    ) {
        RetrieveIntentTask(
            options,
            repository,
            workScope,
            object : ApiResultCallback<AirwallexHttpResponse> {
                override fun onSuccess(result: AirwallexHttpResponse) {
                    if (result.isSuccessful && result.body != null) {
                        val paymentIntent = AirwallexPlugins.gson.fromJson(
                            result.body.string(),
                            PaymentIntent::class.java
                        )
                        callback.onSuccess(paymentIntent)
                    } else {
                        callback.onFailed(handleAPIError(result))
                    }
                }

                override fun onError(e: AirwallexException) {
                    callback.onFailed(e)
                }
            }
        ).execute()
    }

    override fun createPaymentMethod(
        options: AirwallexApiRepository.Options,
        paymentMethodParams: PaymentMethodParams,
        callback: Airwallex.PaymentCallback<PaymentMethod>
    ) {
        CreatePaymentMethodTask(
            options,
            paymentMethodParams,
            repository,
            workScope,
            object : ApiResultCallback<AirwallexHttpResponse> {
                override fun onSuccess(result: AirwallexHttpResponse) {
                    if (result.isSuccessful && result.body != null) {
                        val paymentMethod = AirwallexPlugins.gson.fromJson(
                            result.body.string(),
                            PaymentMethod::class.java
                        )
                        callback.onSuccess(paymentMethod)
                    } else {
                        callback.onFailed(handleAPIError(result))
                    }
                }

                override fun onError(e: AirwallexException) {
                    callback.onFailed(e)
                }
            }
        ).execute()
    }

    override fun getPaymentMethods(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentCallback<PaymentMethodResponse>
    ) {
        GetPaymentMethodsTask(
            options,
            repository,
            workScope,
            object : ApiResultCallback<AirwallexHttpResponse> {
                override fun onSuccess(result: AirwallexHttpResponse) {
                    if (result.isSuccessful && result.body != null) {
                        val response: PaymentMethodResponse = AirwallexPlugins.gson.fromJson(
                            result.body.string(),
                            PaymentMethodResponse::class.java
                        )

                        callback.onSuccess(response)
                    } else {
                        callback.onFailed(handleAPIError(result))
                    }
                }

                override fun onError(e: AirwallexException) {
                    callback.onFailed(e)
                }
            }
        ).execute()
    }

    private fun handleAPIError(result: AirwallexHttpResponse): APIException {
        val error = if (result.body != null) AirwallexPlugins.gson.fromJson(
            result.body.string(),
            AirwallexError::class.java
        ) else null
        return APIException(
            message = null,
            traceId = result.allHeaders["x-awx-traceid"],
            statusCode = result.statusCode,
            error = error,
            e = null
        )
    }

    private class ConfirmIntentTask(
        private val options: AirwallexApiRepository.Options,
        private val paymentIntentParams: PaymentIntentParams,
        private val airwallexRepository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
            return airwallexRepository.confirmPaymentIntent(options, paymentIntentParams)
        }
    }

    private class RetrieveIntentTask(
        private val options: AirwallexApiRepository.Options,
        private val airwallexRepository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
            return airwallexRepository.retrievePaymentIntent(options)
        }
    }

    private class CreatePaymentMethodTask(
        private val options: AirwallexApiRepository.Options,
        private val paymentMethodParams: PaymentMethodParams,
        private val airwallexRepository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
            return airwallexRepository.createPaymentMethod(options, paymentMethodParams)
        }
    }

    private class GetPaymentMethodsTask(
        private val options: AirwallexApiRepository.Options,
        private val airwallexRepository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexHttpResponse>
    ) : ApiOperation<AirwallexHttpResponse>(workScope, callback) {

        override suspend fun getResult(): AirwallexHttpResponse? {
            return airwallexRepository.getPaymentMethods(options)
        }
    }
}
