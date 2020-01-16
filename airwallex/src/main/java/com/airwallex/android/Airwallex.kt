package com.airwallex.android

import android.content.Context
import androidx.annotation.UiThread

class Airwallex internal constructor(
    val context: Context,
    val token: String,
    private val paymentController: PaymentController,
    private val airwallexRepository: AirwallexRepository
) {

    companion object {
        fun initialize(configuration: AirwallexConfiguration) {
            // check configuration params

            AirwallexPlugins.initialize(configuration)
        }
    }

    @JvmOverloads
    constructor(
        context: Context,
        token: String
    ) : this(
        context.applicationContext,
        token,
        AirwallexApiRepository()
    )

    private constructor(
        context: Context,
        token: String,
        airwallexRepository: AirwallexRepository
    ) : this(
        context.applicationContext,
        token,
        AirwallexPaymentController(airwallexRepository),
        airwallexRepository
    )

    @UiThread
    fun confirmPayment(
        paymentIntentId: String
    ) {
        paymentController.startConfirm(paymentIntentId, token)
    }

//    @UiThread
//    fun confirmPayment(
//        fragment: Fragment,
//        confirmPaymentIntentParams: ConfirmPaymentIntentParams
//    ) {
//        paymentController.startConfirmAndAuth(
//            AuthActivityStarter.Host.create(fragment),
//            confirmPaymentIntentParams,
//            ApiRequest.Options(
//                url = config.environment.baseUrl
//            )
//        )
//    }

//    @UiThread
//    fun onPaymentResult(
//        requestCode: Int,
//        data: Intent?,
//        callback: ApiResultCallback<PaymentIntentResult>
//    ): Boolean {
//        return if (data != null && paymentController.shouldHandlePaymentResult(requestCode, data)) {
//            paymentController.handlePaymentResult(
//                data,
//                ApiRequest.Options(
//                    url = config.environment.baseUrl
//                ),
//                callback
//            )
//            true
//        } else {
//            false
//        }
//    }
//
//    @WorkerThread
//    fun retrievePaymentIntentSynchronous(): PaymentIntent? {
//        return stripeRepository.retrievePaymentIntent(
//            ApiRequest.Options(
//                url = config.environment.baseUrl
//            )
//        )
//    }
}