package com.airwallex.android.googlepay

import android.app.Activity
import com.airwallex.android.core.ActionComponentProvider
import com.airwallex.android.core.ActionComponentProviderType
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.wallet.IsReadyToPayRequest
import kotlinx.coroutines.tasks.await

class GooglePayComponentProvider : ActionComponentProvider<GooglePayComponent> {
    private val googlePayComponent: GooglePayComponent by lazy {
        GooglePayComponent()
    }

    override fun get(): GooglePayComponent {
        return googlePayComponent
    }

    override fun getType(): ActionComponentProviderType {
        return ActionComponentProviderType.GOOGLEPAY
    }

    override fun canHandleAction(nextAction: NextAction?): Boolean {
        return false
    }

    override suspend fun canHandleSessionAndPaymentMethod(
        session: AirwallexSession,
        paymentMethodType: AvailablePaymentMethodType,
        activity: Activity
    ): Boolean {
        get().apply {
            this.session = session
            this.paymentMethodType = paymentMethodType
        }
        AirwallexLogger.info("GooglePayComponentProvider canHandleSessionAndPaymentMethod: cardSchemes = ${paymentMethodType.cardSchemes}")
        return requestIsReadyToPay(session, paymentMethodType, activity)
    }

    @Suppress("ReturnCount")
    private suspend fun requestIsReadyToPay(
        session: AirwallexSession,
        paymentMethodType: AvailablePaymentMethodType,
        activity: Activity
    ): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity)
        if (resultCode != ConnectionResult.SUCCESS) {
            AirwallexLogger.error("GooglePayComponentProvider requestIsReadyToPay: resultCode = $resultCode")
            return false
        }

        val options = session.googlePayOptions ?: return false
        val skipIsReadyToPay = options.skipReadinessCheck == true
        AirwallexLogger.info("GooglePayComponentProvider requestIsReadyToPay: skipIsReadyToPay = $skipIsReadyToPay")
        if (skipIsReadyToPay) return true

        val paymentsClient = PaymentsUtil.createPaymentsClient(activity)
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest(
            options,
            paymentMethodType.cardSchemes
        ) ?: return false

        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        val isReadyToPay = paymentsClient.isReadyToPay(request).await()
        AirwallexLogger.info("GooglePayComponentProvider requestIsReadyToPay: isReadyToPay = $isReadyToPay")
        return isReadyToPay
    }
}
