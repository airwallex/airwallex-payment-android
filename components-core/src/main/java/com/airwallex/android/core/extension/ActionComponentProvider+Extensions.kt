package com.airwallex.android.core.extension

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.*
import java.util.*

@Suppress("LongParameterList")
internal fun ActionComponent.confirmGooglePayIntent(
    fragment: Fragment?,
    activity: Activity,
    paymentManager: PaymentManager,
    applicationContext: Context,
    paymentIntentId: String,
    clientSecret: String,
    additionalInfo: Map<String, String>,
    billing: Billing?,
    autoCapture: Boolean,
    listener: Airwallex.PaymentResultListener
) {
    retrieveSecurityToken(
        paymentIntentId, applicationContext,
        object : SecurityTokenListener {
            override fun onResponse(deviceId: String) {
                val device = paymentManager.buildDeviceInfo(deviceId)
                val threeDSecure = ThreeDSecure.Builder()
                    .setReturnUrl(AirwallexPlugins.environment.threeDsReturnUrl()).build()
                val request = PaymentIntentConfirmRequest.Builder(UUID.randomUUID().toString())
                    .setPaymentMethodOptions(
                        PaymentMethodOptions.Builder()
                            .setCardOptions(
                                PaymentMethodOptions.CardOptions.Builder()
                                    .setAutoCapture(autoCapture)
                                    .setThreeDSecure(threeDSecure).build()
                            )
                            .build()
                    )
                    .setDevice(device)
                    .setPaymentMethodRequest(
                        PaymentMethodRequest.Builder(PaymentMethodType.GOOGLEPAY.value)
                            .setGooglePayPaymentMethodRequest(additionalInfo, billing)
                            .build()
                    )
                    .build()
                val options = Options.ConfirmPaymentIntentOptions(
                    clientSecret = clientSecret,
                    paymentIntentId = paymentIntentId,
                    request = request
                )
                paymentManager.startOperation(
                    options,
                    object : Airwallex.PaymentListener<PaymentIntent> {
                        override fun onSuccess(response: PaymentIntent) {
                            if (response.nextAction != null) {
                                val cardNextActionModel = CardNextActionModel(
                                    fragment = fragment,
                                    activity = activity,
                                    paymentManager = paymentManager,
                                    clientSecret = clientSecret,
                                    device = device,
                                    paymentIntentId = response.id,
                                    currency = response.currency,
                                    amount = response.amount
                                )
                                handlePaymentIntentResponse(
                                    response.id,
                                    response.nextAction,
                                    fragment,
                                    activity,
                                    applicationContext,
                                    cardNextActionModel,
                                    listener
                                )
                            } else {
                                listener.onCompleted(AirwallexPaymentStatus.Success(response.id))
                            }
                        }

                        override fun onFailed(exception: AirwallexException) {
                            listener.onCompleted(AirwallexPaymentStatus.Failure(exception))
                        }
                    }
                )
            }
        }
    )
}