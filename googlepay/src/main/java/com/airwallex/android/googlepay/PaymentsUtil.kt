package com.airwallex.android.googlepay

import android.app.Activity
import com.airwallex.android.core.GooglePayOptions
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object PaymentsUtil {
    /**
     * Create a Google Pay API base request object with properties used in all requests.
     *
     * @return Google Pay API base request object.
     * @throws JSONException
     */
    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    /**
     * Describe your app's support for the CARD payment method.
     *
     *
     * The provided properties are applicable to both an IsReadyToPayRequest and a
     * PaymentDataRequest.
     *
     * @return A CARD PaymentMethod object describing accepted cards.
     * @throws JSONException
     * @see [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    // Optionally, you can add billing address/phone number associated with a CARD payment method.
    private fun baseCardPaymentMethod(googlePayOptions: GooglePayOptions, supportedCardSchemes: List<String>?): JSONObject {
        return JSONObject().apply {

            val parameters = JSONObject().apply {
                put("allowedAuthMethods",
                    googlePayOptions.allowedCardAuthMethods ?: Constants.DEFAULT_SUPPORTED_METHODS)
                put("allowedCardNetworks",
                    supportedCardSchemes ?: Constants.DEFAULT_SUPPORTED_CARD_NETWORKS)
                googlePayOptions.billingAddressRequired?.let {
                    put("billingAddressRequired", it)
                }
                googlePayOptions.billingAddressParameters?.let { billingParams ->
                    put("billingAddressParameters", JSONObject().apply {
                        billingParams.format?.let {
                            put("format", it.name)
                        }
                        billingParams.phoneNumberRequired?.let {
                            put("phoneNumberRequired", it)
                        }
                    })
                }
            }

            put("type", "CARD")
            put("parameters", parameters)
        }
    }

    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(Constants.PAYMENTS_ENVIRONMENT)
            .build()

        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay.
     *
     * @return API version and payment methods supported by the app.
     * @see [IsReadyToPayRequest](https://developers.google.com/pay/api/android/reference/object.IsReadyToPayRequest)
     */
    fun isReadyToPayRequest(googlePayOptions: GooglePayOptions, supportedCardSchemes: List<String>?): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods",
                    JSONArray().put(baseCardPaymentMethod(googlePayOptions, supportedCardSchemes)))
            }

        } catch (e: JSONException) {
            null
        }
    }
}