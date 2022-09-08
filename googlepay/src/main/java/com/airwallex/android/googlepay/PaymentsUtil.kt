package com.airwallex.android.googlepay

import android.app.Activity
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.CardScheme
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal

object PaymentsUtil {
    /**
     * Create a Google Pay API base request object with properties used in all requests.
     *
     * @return Google Pay API base request object.
     * @throws JSONException
     */
    private fun baseRequest(): JSONObject {
        return JSONObject().apply {
            apply {
                put("apiVersion", 2)
                put("apiVersionMinor", 0)
            }
        }
    }

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
    private fun gatewayTokenizationSpecification(merchantId: String): JSONObject {
        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put(
                "parameters",
                JSONObject(
                    mapOf(
                        "gateway" to Constants.PAYMENT_GATEWAY_TOKENIZATION_NAME,
                        "gatewayMerchantId" to merchantId
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
                            ?: Constants.DEFAULT_SUPPORTED_METHODS
                    )
                )
                put(
                    "allowedCardNetworks",
                    JSONArray(cardList ?: Constants.DEFAULT_SUPPORTED_CARD_NETWORKS)
                )
                googlePayOptions.allowPrepaidCards?.let {
                    put("allowPrepaidCards", it)
                }
                googlePayOptions.allowCreditCards?.let {
                    put("allowCreditCards", it)
                }
                googlePayOptions.assuranceDetailsRequired?.let {
                    put("assuranceDetailsRequired", it)
                }
                googlePayOptions.billingAddressRequired?.let {
                    put("billingAddressRequired", it)
                }
                googlePayOptions.billingAddressParameters?.let { billingParams ->
                    put(
                        "billingAddressParameters",
                        JSONObject().apply {
                            billingParams.format?.let {
                                put("format", it.name)
                            }
                            billingParams.phoneNumberRequired?.let {
                                put("phoneNumberRequired", it)
                            }
                        }
                    )
                }
            }

            put("type", "CARD")
            put("parameters", parameters)
        }
    }

    /**
     * Describe the expected returned payment data for the CARD payment method
     *
     * @return A CARD PaymentMethod describing accepted cards and optional fields.
     * @throws JSONException
     * @see [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    private fun cardPaymentMethod(
        googlePayOptions: GooglePayOptions,
        cardList: List<String>?
    ): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod(googlePayOptions, cardList)
        cardPaymentMethod.put(
            "tokenizationSpecification",
            gatewayTokenizationSpecification(googlePayOptions.merchantId)
        )

        return cardPaymentMethod
    }

    /**
     * Creates an instance of [PaymentsClient] for use in an [Activity] using the
     * environment and theme set in [Constants].
     *
     * @param activity is the caller's activity.
     */
    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(AirwallexPlugins.environment.googlePayEnvironment())
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
    fun isReadyToPayRequest(
        googlePayOptions: GooglePayOptions,
        supportedCardSchemes: List<CardScheme>?
    ): JSONObject? {
        return try {
            baseRequest().apply {
                put(
                    "allowedPaymentMethods",
                    JSONArray().put(
                        baseCardPaymentMethod(
                            googlePayOptions,
                            supportedCardSchemes?.map { it.name.uppercase() }
                        )
                    )
                )
            }
        } catch (e: JSONException) {
            null
        }
    }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status.
     *
     * @return information about the requested payment.
     * @throws JSONException
     * @see [TransactionInfo](https://developers.google.com/pay/api/android/reference/object.TransactionInfo)
     */
    private fun getTransactionInfo(
        price: String,
        countryCode: String,
        currency: String,
        googlePayOptions: GooglePayOptions
    ): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("countryCode", countryCode)
            put("currencyCode", currency)
            googlePayOptions.transactionId?.let {
                put("transactionId", it)
            }
            put("totalPriceLabel", googlePayOptions.totalPriceLabel ?: "order.total")
            googlePayOptions.checkoutOption?.let {
                put("checkoutOption", it)
            }
        }
    }

    /**
     * An object describing information requested in a Google Pay payment sheet
     *
     * @return Payment data expected by your app.
     * @see [PaymentDataRequest](https://developers.google.com/pay/api/android/reference/object.PaymentDataRequest)
     */
    fun getPaymentDataRequest(
        price: BigDecimal,
        countryCode: String,
        currency: String,
        googlePayOptions: GooglePayOptions,
        supportedCardSchemes: List<CardScheme>?
    ): JSONObject? {
        return try {
            baseRequest().apply {
                googlePayOptions.merchantName?.let {
                    put(
                        "merchantInfo",
                        JSONObject().apply {
                            put("merchantName", it)
                        }
                    )
                }
                put(
                    "allowedPaymentMethods",
                    JSONArray().put(
                        cardPaymentMethod(
                            googlePayOptions,
                            supportedCardSchemes?.map { it.name.uppercase() }
                        )
                    )
                )
                put(
                    "transactionInfo",
                    getTransactionInfo(
                        price.toString(),
                        countryCode,
                        currency,
                        googlePayOptions
                    )
                )
                googlePayOptions.emailRequired?.let {
                    put("emailRequired", it)
                }
                // An optional shipping address requirement is a top-level property of the
                // PaymentDataRequest JSON object.
                googlePayOptions.shippingAddressRequired?.let {
                    put("shippingAddressRequired", it)
                }
                googlePayOptions.shippingAddressParameters?.let { shippingParams ->
                    put(
                        "shippingAddressParameters",
                        JSONObject().apply {
                            shippingParams.allowedCountryCodes?.let {
                                put("allowedCountryCodes", JSONArray(it))
                            }
                            shippingParams.phoneNumberRequired?.let {
                                put("phoneNumberRequired", it)
                            }
                        }
                    )
                }
            }
        } catch (e: JSONException) {
            null
        }
    }

    fun getBilling(payload: JSONObject): Billing? {
        val name = payload.optString("name")
        val countryCode = payload.optString("countryCode")
        if (name.isNotEmpty() && countryCode.isNotEmpty()) {
            val street = listOf(
                payload.optString("address1"),
                payload.optString("address2"),
                payload.optString("address3")
            ).filterNot { it.isEmpty() }.joinToString(" ")
            val locality = payload.optString("locality").takeIf { it.isNotEmpty() }
            return Billing.Builder()
                .setAddress(
                    Address.Builder()
                        .setCity(locality ?: countryCode)
                        .setCountryCode(countryCode)
                        .setPostcode(payload.optString("postalCode", null))
                        .setState(payload.optString("administrativeArea", null))
                        .setStreet(street)
                        .build()
                )
                .setFirstName(name.split(" ").firstOrNull() ?: "")
                .setLastName(name.split(" ").getOrNull(1) ?: "")
                .setEmail(payload.optString("email", null))
                .build()
        } else {
            return null
        }
    }
}
