package com.airwallex.android

import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentIntentParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal class AirwallexPaymentController(
    private val airwallexRepository: ApiRepository,
    private val workScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PaymentController {

    override fun startConfirm(
        options: AirwallexApiRepository.Options,
        paymentIntentParams: PaymentIntentParams,
        callback: Airwallex.PaymentIntentCallback
    ) {
        ConfirmIntentTask(
            options,
            paymentIntentParams,
            airwallexRepository,
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
                        callback.onFailed()
                    }
                }

                override fun onError(e: Exception) {
                    callback.onFailed()
                }

            }
        ).execute()
    }

    override fun retrievePaymentIntent(
        options: AirwallexApiRepository.Options,
        callback: Airwallex.PaymentIntentCallback
    ) {
        RetrieveIntentTask(
            options,
            airwallexRepository,
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
                        callback.onFailed()
                    }
                }

                override fun onError(e: Exception) {
                    callback.onFailed()
                }

            }
        ).execute()
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
}