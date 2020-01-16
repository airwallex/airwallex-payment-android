package com.airwallex.android

import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal class AirwallexPaymentController(
    private val airwallexRepository: ApiRepository,
    private val workScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PaymentController {

    override fun startConfirm(paymentIntentId: String, token: String) {
        ConfirmIntentTask(
            token,
            paymentIntentId,
            airwallexRepository,
            workScope,
            ConfirmIntentCallback()
        ).execute()
    }

    private class ConfirmIntentTask(
        private val token: String,
        private val paymentIntentId: String,
        private val airwallexRepository: ApiRepository,
        workScope: CoroutineScope,
        callback: ApiResultCallback<AirwallexIntent>
    ) : ApiOperation<AirwallexIntent>(workScope, callback) {

        override suspend fun getResult(): AirwallexIntent? {
            return airwallexRepository.confirmPaymentIntent(token, paymentIntentId)
        }
    }

    private class ConfirmIntentCallback : ApiResultCallback<AirwallexIntent> {

        override fun onError(e: Exception) {
        }

        override fun onSuccess(result: AirwallexIntent) {

            Log.e("aaa", "result $result")
        }
    }

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