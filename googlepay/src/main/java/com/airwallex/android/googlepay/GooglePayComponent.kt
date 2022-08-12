package com.airwallex.android.googlepay

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.airwallex.android.core.ActionComponent
import com.airwallex.android.core.ActionComponentProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.CardNextActionModel
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentDataRequest

class GooglePayComponent : ActionComponent {
    companion object {
        val PROVIDER: ActionComponentProvider<GooglePayComponent> = GooglePayComponentProvider()
    }

    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991
    lateinit var paymentMethodType: AvailablePaymentMethodType
    lateinit var session: AirwallexSession

    override fun handlePaymentIntentResponse(
        paymentIntentId: String,
        nextAction: NextAction?,
        activity: Activity,
        applicationContext: Context,
        cardNextActionModel: CardNextActionModel?,
        listener: Airwallex.PaymentResultListener
    ) {
        val googlePayOptions = session.googlePayOptions ?: return
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(
            priceCemts = session.amount.toLong(),
            countryCode = session.countryCode,
            currency = session.currency,
            googlePayOptions = googlePayOptions,
            supportedCardSchemes = paymentMethodType.cardSchemes?.let { cardSchemes ->
                cardSchemes.map { it.name.uppercase() }
            }
        ) ?: run {
            listener.onCompleted(
                AirwallexPaymentStatus.Failure(
                    AirwallexCheckoutException(
                        message = "Can't serialize Google Pay payment data request"
                    )
                )
            )
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        val paymentClient = PaymentsUtil.createPaymentsClient(activity)
        AutoResolveHelper.resolveTask(
            paymentClient.loadPaymentData(request),
            activity,
            LOAD_PAYMENT_DATA_REQUEST_CODE
        )
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        // Since only card payments require a device ID, this will not be executed
        securityTokenListener.onResponse("")
    }
}
