package com.airwallex.android.view.util

import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.TokenManager
import com.airwallex.android.core.googlePaySupportedNetworks
import com.airwallex.android.core.model.CardScheme
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object GooglePayUtil {
    /**
     * Gateway Integration: Identify your gateway (Airwallex) and your app's Airwallex merchant identifier.
     *
     *
     * The Google Pay API response will return an encrypted payment method capable of being charged
     * by a supported gateway after payer authorization.
     *
     *
     * @return Payment data tokenization for the CARD payment method.
     * @throws JSONException
     * @see [PaymentMethodTokenizationSpecification](https://developers.google.com/pay/api/android/reference/object.PaymentMethodTokenizationSpecification)
     */
    private fun gatewayTokenizationSpecification(): JSONObject {
        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put(
                "parameters",
                JSONObject(
                    mapOf(
                        "gateway" to GooglePayConstants.PAYMENT_GATEWAY_TOKENIZATION_NAME,
                        "gatewayMerchantId" to (TokenManager.accountId ?: "")
                    )
                )
            )
        }
    }

    /**
     * Describe your app's support for the CARD payment method.
     *
     *
     * @param googlePayOptions is the merchant provided value when integrates SDK
     * @param supportedCardScheme is the merchant's supported card types
     * @return A CARD PaymentMethod object describing accepted cards.
     * @throws JSONException
     * @see [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    // Optionally, you can add billing address/phone number associated with a CARD payment method.
    private fun baseCardPaymentMethod(
        googlePayOptions: GooglePayOptions,
        cardList: List<String>?
    ): JSONObject {
        return JSONObject().apply {
            val parameters = JSONObject().apply {
                put(
                    "allowedAuthMethods",
                    JSONArray(
                        googlePayOptions.allowedCardAuthMethods
                            ?: GooglePayConstants.DEFAULT_SUPPORTED_METHODS
                    )
                )
                put(
                    "allowedCardNetworks",
                    JSONArray(cardList.takeIf { !it.isNullOrEmpty() }?.filterNot { it == "VISA" } ?: GooglePayConstants.DEFAULT_SUPPORTED_CARD_NETWORKS)
                )
                googlePayOptions.allowPrepaidCards?.let {
                    put("allowPrepaidCards", it)
                }
                googlePayOptions.allowCreditCards?.let {
                    put("allowCreditCards", it)
                }
            }

            put("type", "CARD")
            put("parameters", parameters)
            put(
                "tokenizationSpecification",
                gatewayTokenizationSpecification()
            )
        }
    }

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay.
     *
     * @return API version and payment methods supported by the app.
     * @see [IsReadyToPayRequest](https://developers.google.com/pay/api/android/reference/object.IsReadyToPayRequest)
     */
    fun retrieveAllowedPaymentMethods(
        googlePayOptions: GooglePayOptions,
        supportedCardSchemes: List<CardScheme>?
    ): JSONArray? {
        return try {
            JSONArray().put(
                baseCardPaymentMethod(
                    googlePayOptions,
                    supportedCardSchemes?.map { it.name.uppercase() }
                )
            )
        } catch (e: JSONException) {
            null
        }
    }
}

object GooglePayConstants {
    const val GOOGLE_PAY_NAME = "googlepay"

    val DEFAULT_SUPPORTED_METHODS = listOf(
        "PAN_ONLY",
        "CRYPTOGRAM_3DS"
    )

    val DEFAULT_SUPPORTED_CARD_NETWORKS = googlePaySupportedNetworks()

    const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "airwallex"
}