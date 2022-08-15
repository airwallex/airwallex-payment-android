package com.airwallex.android.googlepay

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
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
import com.airwallex.android.core.log.Logger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import org.json.JSONException
import org.json.JSONObject

class GooglePayComponent : ActionComponent {
    companion object {
        val PROVIDER: ActionComponentProvider<GooglePayComponent> = GooglePayComponentProvider()
    }

    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991
    private val ERROR_TAG = "Google Pay loadPaymentData failed"

    private var listener: Airwallex.PaymentResultListener? = null
    private var paymentIntentId: String? = null
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
        this.paymentIntentId = paymentIntentId
        this.listener = listener
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
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val id = paymentIntentId ?: run {
                        Logger.error(
                            ERROR_TAG,
                            String.format(ERROR_TAG, "Invalid payment intent ID")
                        )
                        listener?.onCompleted(AirwallexPaymentStatus.Cancel)
                        return false
                    }
                    data?.let { intent ->
                        PaymentData.getFromIntent(intent)?.let(::createPaymentSuccessInfo)?.let {
                            listener?.onCompleted(AirwallexPaymentStatus.Success(id, it))
                        } ?: listener?.onCompleted(AirwallexPaymentStatus.Cancel)
                    } ?: listener?.onCompleted(AirwallexPaymentStatus.Cancel)
                }

                RESULT_CANCELED -> {
                    listener?.onCompleted(AirwallexPaymentStatus.Cancel)
                }
                /**
                 * At this stage, the user has already seen a popup informing them an error occurred,
                 * so only logging is required.
                 *
                 * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
                 * WalletConstants.ERROR_CODE_* constants.
                 * @see [
                 * Wallet Constants Library](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants.constant-summary)
                 */
                AutoResolveHelper.RESULT_ERROR -> {
                    AutoResolveHelper.getStatusFromIntent(data)?.let {
                        Logger.error(ERROR_TAG, String.format("Error code: %d", it.statusCode))
                    }
                    listener?.onCompleted(AirwallexPaymentStatus.Cancel)
                }
            }
            return true
        }
        return false
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        // Since only card payments require a device ID, this will not be executed
        securityTokenListener.onResponse("")
    }

    private fun createPaymentSuccessInfo(paymentData: PaymentData): Map<String, Any>? {
        val paymentInformation = paymentData.toJson()
        val info = mutableMapOf<String, Any>().apply {
            try {
                val paymentMethodData =
                    JSONObject(paymentInformation).getJSONObject("paymentMethodData")
                put("payment_data_type", "encrypted_payment_token")
                // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
                put(
                    "encrypted_payment_token",
                    paymentMethodData.getJSONObject("tokenizationData").getString("token")
                )
                paymentMethodData.optJSONObject("info").optJSONObject("billingAddress")?.let { billingAddress ->
                    PaymentsUtil.getBilling(billingAddress)?.let {
                        put("billing", it)
                    }
                }
            } catch (e: JSONException) {
                Logger.error(ERROR_TAG, "Error: ${e.message}")
                return null
            }
        }
        return info
    }
}
