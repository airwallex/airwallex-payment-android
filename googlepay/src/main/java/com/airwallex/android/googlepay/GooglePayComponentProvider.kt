package com.airwallex.android.googlepay

import android.app.Activity
import com.airwallex.android.core.ActionComponentProvider
import com.airwallex.android.core.ActionComponentProviderType
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.Logger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
        TODO("Not yet implemented")
    }

    override suspend fun canHandleSessionAndPaymentMethod(
        session: AirwallexSession,
        paymentMethodType: AvailablePaymentMethodType,
        activity: Activity
    ): Boolean {
        return requestIsReadyToPay(session, paymentMethodType, activity)
    }

    private suspend fun requestIsReadyToPay(
        session: AirwallexSession,
        paymentMethodType: AvailablePaymentMethodType,
        activity: Activity
    ): Boolean = suspendCoroutine { cont ->
        session.googlePayOptions?.let { options ->
            val paymentsClient = PaymentsUtil.createPaymentsClient(activity)
            PaymentsUtil.isReadyToPayRequest(
                options,
                paymentMethodType.cardSchemes?.let { cardSchemes ->
                    cardSchemes.map { it.name }
                }
            )?.let { isReadyToPayJson ->
                val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
                val task = paymentsClient.isReadyToPay(request)
                task.addOnCompleteListener { completedTask ->
                    try {
                        completedTask.getResult(ApiException::class.java)?.let {
                            cont.resume(it)
                        } ?: cont.resume(false)
                    } catch (exception: ApiException) {
                        // Process error
                        Logger.error("isReadyToPay failed", exception)
                        cont.resume(false)
                    }
                }
            } ?: cont.resume(false)
        } ?: cont.resume(false)
    }
}