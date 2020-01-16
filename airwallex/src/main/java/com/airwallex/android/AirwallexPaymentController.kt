package com.airwallex.android

import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal class AirwallexPaymentController(
    private val airwallexRepository: AirwallexRepository,
    private val workScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PaymentController {

//    override fun startConfirmAndAuth(
//        confirmStripeIntentParams: ConfirmAirwallexIntentParams
//    ) {
//        ConfirmAirwallexIntentTask(
//            stripeRepository, confirmStripeIntentParams, requestOptions, workScope,
//            ConfirmStripeIntentCallback(
//                host, requestOptions, this, getRequestCode(confirmStripeIntentParams)
//            )
//        ).execute()
//    }


    private class ConfirmAirwallexIntentTask(
        private val token: String,
        private val paymentIntentId: String,
        private val airwallexRepository: AirwallexRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexIntent>
    ) : ApiOperation<AirwallexIntent>(workScope, callback) {

        override suspend fun getResult(): AirwallexIntent? {
            return airwallexRepository.confirmPaymentIntent(token, paymentIntentId)
        }
    }

    override fun startConfirm(paymentIntentId: String, token: String) {
        ConfirmAirwallexIntentTask(
            token,
            paymentIntentId,
            airwallexRepository,
            workScope,
            ConfirmAirwallexIntentCallback()
        ).execute()
    }

    private class ConfirmAirwallexIntentCallback : ApiResultCallback<AirwallexIntent> {

        override fun onError(e: Exception) {
        }

        override fun onSuccess(result: AirwallexIntent) {

            Log.e("aaa", "result $result")
        }
    }

//    private class ConfirmAirwallexIntentTask(
//        private val stripeRepository: AirwallexRepository,
//        params: ConfirmAirwallexIntentParams,
//        private val requestOptions: ApiRequest.Options,
//        workScope: CoroutineScope,
//        callback: ApiResultCallback<AirwallexIntent>
//    ) : ApiOperation<AirwallexIntent>(workScope, callback) {
//        private val params: ConfirmAirwallexIntentParams =
//            params.withShouldUseStripeSdk(shouldUseStripeSdk = true)
//
//        override suspend fun getResult(): AirwallexIntent? {
//            return stripeRepository.confirmPaymentIntent(params, requestOptions)
//        }
//    }

    override fun shouldHandlePaymentResult(requestCode: Int, data: Intent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handlePaymentResult(
        data: Intent,
        callback: ApiResultCallback<PaymentIntentResult>
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}