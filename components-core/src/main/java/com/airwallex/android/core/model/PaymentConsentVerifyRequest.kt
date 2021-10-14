package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/**
 * Params for create a [PaymentConsent]
 */
@Parcelize
data class PaymentConsentVerifyRequest internal constructor(
    /**
     * Unique request ID specified by the merchant
     */
    val requestId: String? = null,

    /**
     * Additional information needed to verify a PaymentConsent
     */
    val verificationOptions: VerificationOptions? = null,

    /**
     * The URL to which your customer will be redirected after they verify PaymentConsent on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively provide an application URI scheme.
     */
    val returnUrl: String? = null,
) : AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_VERIFICATION_OPTIONS = "verification_options"
        private const val FIELD_RETURN_URL = "return_url"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                requestId?.let {
                    mapOf(FIELD_REQUEST_ID to it)
                }.orEmpty()
            )
            .plus(
                verificationOptions?.let {
                    mapOf(FIELD_VERIFICATION_OPTIONS to verificationOptions.toParamMap())
                }.orEmpty()
            )
            .plus(
                returnUrl?.let {
                    mapOf(FIELD_RETURN_URL to it)
                }.orEmpty()
            )
    }

    @Parcelize
    data class VerificationOptions(
        /**
         * Type of the payment method
         */
        val type: String,

        /**
         * Card information for verifying PaymentConsent
         */
        val cardOptions: CardVerificationOptions? = null,

        /**
         * Redirect information for verifying PaymentConsent.
         */
        val thirdPartOptions: ThirdPartVerificationOptions? = null
    ) : AirwallexRequestModel, Parcelable {

        override fun toParamMap(): Map<String, Any> {
            return mapOf<String, Any>()
                .plus(
                    cardOptions?.let {
                        mapOf(type to it.toParamMap())
                    }.orEmpty()
                )
                .plus(
                    thirdPartOptions?.let {
                        mapOf(type to it.toParamMap())
                    }.orEmpty()
                )
        }
    }

    @Parcelize
    data class ThirdPartVerificationOptions(
        /**
         * Refer to the specification of the verification_options of the payment method.
         */
        val flow: AirwallexPaymentRequestFlow? = null,

        /**
         * Can be one of ios, android. osType must be set when flow is mweb, inapp.
         */
        val osType: String? = null

    ) : AirwallexRequestModel, Parcelable {

        private companion object {
            private const val FIELD_FLOW = "flow"
            private const val FIELD_OS_TYPE = "os_type"
        }

        override fun toParamMap(): Map<String, Any> {
            return mapOf<String, Any>()
                .plus(
                    mapOf(FIELD_FLOW to AirwallexPaymentRequestFlow.IN_APP.value)
                )
                .plus(
                    mapOf(FIELD_OS_TYPE to "android")
                )
        }
    }

    @Parcelize
    data class CardVerificationOptions(
        /**
         * The alternative amount of verification if zero amount is not acceptable for the provider. The transaction of this amount should be reverted once the verification process finished. Must be greater than 0.
         */
        val amount: BigDecimal? = null,

        /**
         * Currency of the initial PaymentIntent to verify the PaymentConsent. Three-letter ISO currency code. Must be a supported currency
         */
        val currency: String? = null,

        /**
         * When requires_cvc for the PaymentConsent is true, this attribute must be provided in order to confirm successfully
         */
        val cvc: String? = null

    ) : AirwallexRequestModel, Parcelable {

        private companion object {
            private const val FIELD_AMOUNT = "amount"
            private const val FIELD_CURRENCY = "currency"
            private const val FIELD_CVC = "cvc"
        }

        override fun toParamMap(): Map<String, Any> {
            return mapOf<String, Any>()
                .plus(
                    amount?.let {
                        mapOf(FIELD_AMOUNT to it)
                    }.orEmpty()
                )
                .plus(
                    currency?.let {
                        mapOf(FIELD_CURRENCY to it)
                    }.orEmpty()
                )
                .plus(
                    cvc?.let {
                        mapOf(FIELD_CVC to it)
                    }.orEmpty()
                )
        }
    }

    class Builder : ObjectBuilder<PaymentConsentVerifyRequest> {
        private var requestId: String? = null
        private var verificationOptions: VerificationOptions? = null
        private var returnUrl: String? = null

        fun setRequestId(requestId: String?): Builder = apply {
            this.requestId = requestId
        }

        fun setVerificationOptions(verificationOptions: VerificationOptions?): Builder = apply {
            this.verificationOptions = verificationOptions
        }

        fun setReturnUrl(returnUrl: String?): Builder = apply {
            this.returnUrl = returnUrl
        }

        override fun build(): PaymentConsentVerifyRequest {
            return PaymentConsentVerifyRequest(
                requestId = requestId,
                verificationOptions = verificationOptions,
                returnUrl = returnUrl
            )
        }
    }
}
