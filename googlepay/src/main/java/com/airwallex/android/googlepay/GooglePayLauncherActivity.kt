package com.airwallex.android.googlepay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.Billing
import com.airwallex.android.ui.extension.getExtraArgs
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.contract.ApiTaskResult
import com.google.android.gms.wallet.contract.TaskResultContracts.GetPaymentDataResult
import org.json.JSONException
import org.json.JSONObject

class GooglePayLauncherActivity : ComponentActivity() {
    // A client for interacting with the Google Pay API.
    private val paymentsClient: PaymentsClient by lazy {
        PaymentsUtil.createPaymentsClient(this)
    }

    private val args: GooglePayActivityLaunch.Args by lazy {
        intent.getExtraArgs()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googlePayLauncher = registerForActivityResult(GetPaymentDataResult()) {
            onGooglePayResult(it)
        }

        val task = getLoadPaymentDataTask()
        task.addOnCompleteListener(googlePayLauncher::launch)
    }

    private fun getLoadPaymentDataTask(): Task<PaymentData> {
        val session = args.session
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(
            price = session.amount,
            countryCode = session.countryCode,
            currency = session.currency,
            googlePayOptions = args.googlePayOptions,
            supportedCardSchemes = args.paymentMethodType.cardSchemes
        )
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }

    private fun onGooglePayResult(taskResult: ApiTaskResult<PaymentData>) {
        when (taskResult.status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                val result = taskResult.result
                if (result != null) {
                    val successInfo = createPaymentSuccessInfo(result)
                    if (successInfo != null) {
                        finishWithResult(
                            GooglePayActivityLaunch.Result.Success(successInfo)
                        )
                    } else {
                        finishWithResult(
                            GooglePayActivityLaunch.Result.Failure(
                                AirwallexCheckoutException(message = "Missing Google Pay token response")
                            )
                        )
                    }
                } else {

                }
            }
            CommonStatusCodes.CANCELED -> finishWithResult(
                GooglePayActivityLaunch.Result.Cancel
            )
        }
    }

    private fun finishWithResult(result: GooglePayActivityLaunch.Result) {
        setResult(
            RESULT_OK,
            Intent()
                .putExtras(
                    result.toBundle()
                )
        )
        finish()
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
                paymentMethodData.optJSONObject("info").optJSONObject("billingAddress")
                    ?.let { billingAddress ->
                        PaymentsUtil.getBilling(billingAddress)?.let {
                            put("billing", it)
                        }
                    }
            } catch (e: JSONException) {

                return null
            }
        }
        return info
    }
}