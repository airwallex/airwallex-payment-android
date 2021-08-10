package com.airwallex.android.redirect

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexApiRepository
import com.airwallex.android.core.ComponentProvider
import com.airwallex.android.core.PaymentManager
import com.airwallex.android.core.exception.APIException
import com.airwallex.android.core.model.*
import java.math.BigDecimal
import java.util.*

@Suppress("unused")
class RedirectComponentProvider internal constructor(
    private val applicationContext: Context,
    private val paymentManager: PaymentManager
) : ComponentProvider {

    constructor(fragment: Fragment, paymentManager: PaymentManager) : this(
        fragment.requireContext().applicationContext,
        paymentManager
    )

    constructor(activity: Activity, paymentManager: PaymentManager) : this(
        activity.applicationContext,
        paymentManager
    )

    override fun buildConfirmPaymentIntentOptions(
        params: ConfirmPaymentIntentParams,
        device: Device?
    ): Options {
        val paymentConsentReference: PaymentConsentReference?
        val paymentMethodRequest: PaymentMethodRequest?

        if (params.paymentConsentId != null) {
            paymentConsentReference = PaymentConsentReference.Builder()
                .setId(params.paymentConsentId)
                .build()
            paymentMethodRequest = null
        } else {
            paymentConsentReference = null
            val builder = PaymentMethodRequest.Builder(params.paymentMethodType)
            val pproInfo = params.pproAdditionalInfo
            if (pproInfo != null) {
                builder.setThirdPartyPaymentMethodRequest(
                    pproInfo.name,
                    pproInfo.email,
                    pproInfo.phone,
                    if (pproInfo.bank != null) pproInfo.bank!!.currency else params.currency,
                    pproInfo.bank
                )
            } else {
                builder.setThirdPartyPaymentMethodRequest()
            }
            paymentMethodRequest = builder.build()
        }
        val request = PaymentIntentConfirmRequest.Builder(
            requestId = UUID.randomUUID().toString()
        )
            .setPaymentMethodRequest(paymentMethodRequest)
            .setCustomerId(params.customerId)
            .setDevice(device)
            .setPaymentConsentReference(paymentConsentReference)
            .build()

        return AirwallexApiRepository.ConfirmPaymentIntentOptions(
            clientSecret = params.clientSecret,
            paymentIntentId = params.paymentIntentId,
            request = request
        )
    }

    override fun handlePaymentIntentResponse(
        clientSecret: String,
        nextAction: NextAction?,
        device: Device?,
        paymentIntentId: String,
        currency: String,
        amount: BigDecimal,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        val redirectUrl = nextAction?.url
        if (redirectUrl.isNullOrEmpty()) {
            listener.onFailed(APIException(message = "Server error, redirect url is null"))
            return
        }
        listener.onNextActionWithRedirectUrl(redirectUrl)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }
}
