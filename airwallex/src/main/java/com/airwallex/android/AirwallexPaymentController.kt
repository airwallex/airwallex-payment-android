package com.airwallex.android

import android.content.Intent
import kotlinx.coroutines.CoroutineScope

internal class AirwallexPaymentController : PaymentController {

    override fun startConfirmAndAuth(
        host: AuthActivityStarter.Host,
        confirmStripeIntentParams: ConfirmAirwallexIntentParams,
        requestOptions: ApiRequest.Options
    ) {
//        ConfirmAirwallexIntentTask(
//            stripeRepository, confirmStripeIntentParams, requestOptions, workScope,
//            ConfirmStripeIntentCallback(
//                host, requestOptions, this, getRequestCode(confirmStripeIntentParams)
//            )
//        ).execute()

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
        requestOptions: ApiRequest.Options,
        callback: ApiResultCallback<PaymentIntentResult>
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}