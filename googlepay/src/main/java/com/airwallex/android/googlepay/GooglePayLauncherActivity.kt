package com.airwallex.android.googlepay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.extension.putIfNotNull
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.ui.extension.getExtraArgs
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.contract.ApiTaskResult
import com.google.android.gms.wallet.contract.TaskResultContracts.GetPaymentDataResult
import org.json.JSONObject

class GooglePayLauncherActivity : ComponentActivity() {
    private val viewModel: GooglePayLauncherViewModel by lazy {
        ViewModelProvider(
            this,
            GooglePayLauncherViewModel.Factory(application, args)
        )[GooglePayLauncherViewModel::class.java]
    }

    private val args: GooglePayActivityLaunch.Args by lazy {
        intent.getExtraArgs()
    }

    private val googlePayLauncher = registerForActivityResult(GetPaymentDataResult()) {
        onGooglePayResult(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val task = viewModel.getLoadPaymentDataTask()
        task.addOnCompleteListener(googlePayLauncher::launch)
    }

    private fun onGooglePayResult(taskResult: ApiTaskResult<PaymentData>) {
        when (taskResult.status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                val result = taskResult.result
                if (result != null) {
                    try {
                        val successInfo = createPaymentSuccessInfo(result)
                        finishWithResult(
                            GooglePayActivityLaunch.Result.Success(successInfo)
                        )
                    } catch (e: Throwable) {
                        val exception = AirwallexCheckoutException(
                            message = "Google Pay missing token data",
                            e = e
                        )
                        AnalyticsLogger.logError(
                            "googlepay_payment_data_retrieve",
                            exception = exception
                        )
                        finishWithResult(GooglePayActivityLaunch.Result.Failure(exception))
                    }
                } else {
                    val exception =
                        AirwallexCheckoutException(message = "Google Pay missing result data")
                    AnalyticsLogger.logError(
                        "googlepay_payment_data_retrieve",
                        exception = exception
                    )
                    finishWithResult(GooglePayActivityLaunch.Result.Failure(exception))
                }
            }

            CommonStatusCodes.CANCELED -> finishWithResult(
                GooglePayActivityLaunch.Result.Cancel
            )

            AutoResolveHelper.RESULT_ERROR -> {
                val status = taskResult.status
                val statusMessage = status.statusMessage.orEmpty()
                val statusCode = status.statusCode.toString()
                AnalyticsLogger.logError(
                    "googlepay_payment_data_retrieve",
                    mutableMapOf("code" to statusCode).apply {
                        putIfNotNull("message", statusMessage)
                    }
                )
                finishWithResult(
                    GooglePayActivityLaunch.Result.Failure(
                        AirwallexCheckoutException(message = "Google Pay failed with error $statusCode: $statusMessage")
                    )
                )
            }

            else -> {
                val exception =
                    AirwallexCheckoutException(message = "Google Pay returned an unexpected result code.")
                AnalyticsLogger.logError("googlepay_payment_data_retrieve", exception = exception)
                finishWithResult(GooglePayActivityLaunch.Result.Failure(exception))
            }
        }
    }

    private fun finishWithResult(result: GooglePayActivityLaunch.Result) {
        AirwallexLogger.info("GooglePayLauncherActivity finishWithResult")
        setResult(
            RESULT_OK,
            Intent()
                .putExtras(
                    result.toBundle()
                )
        )
        finish()
    }

    private fun createPaymentSuccessInfo(paymentData: PaymentData): Map<String, Any> {
        val paymentInformation = paymentData.toJson()
        val info = mutableMapOf<String, Any>().apply {
            runCatching {
                val paymentMethodData =
                    JSONObject(paymentInformation).getJSONObject("paymentMethodData")
                put("payment_data_type", "encrypted_payment_token")
                // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
                put(
                    "encrypted_payment_token",
                    paymentMethodData.getJSONObject("tokenizationData").getString("token")
                )
                paymentMethodData.optJSONObject("info")?.optJSONObject("billingAddress")
                    ?.let { billingAddress ->
                        putIfNotNull("billing", PaymentsUtil.getBilling(billingAddress))
                    }
            }
        }
        return info
    }
}