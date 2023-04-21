package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class Options(
    open val clientSecret: String
) : Parcelable {
    data class RetrievePaymentIntentOptions(
        override val clientSecret: String,
        internal val paymentIntentId: String
    ) : Options(clientSecret)

    data class ConfirmPaymentIntentOptions(
        override val clientSecret: String,
        internal val paymentIntentId: String,
        internal val request: PaymentIntentConfirmRequest
    ) : Options(clientSecret = clientSecret)

    data class ContinuePaymentIntentOptions(
        override val clientSecret: String,
        internal val paymentIntentId: String,
        internal val request: PaymentIntentContinueRequest
    ) : Options(clientSecret = clientSecret)

    data class CreatePaymentMethodOptions(
        override val clientSecret: String,
        internal val request: PaymentMethodCreateRequest
    ) : Options(clientSecret = clientSecret)

    data class CreatePaymentConsentOptions(
        override val clientSecret: String,
        internal val request: PaymentConsentCreateRequest
    ) : Options(clientSecret = clientSecret)

    data class VerifyPaymentConsentOptions(
        override val clientSecret: String,
        internal val paymentConsentId: String,
        internal val request: PaymentConsentVerifyRequest
    ) : Options(clientSecret = clientSecret)

    data class DisablePaymentConsentOptions constructor(
        override val clientSecret: String,
        internal val paymentConsentId: String,
        internal val request: PaymentConsentDisableRequest
    ) : Options(clientSecret = clientSecret)

    data class RetrievePaymentConsentOptions constructor(
        override val clientSecret: String,
        internal val paymentConsentId: String
    ) : Options(clientSecret = clientSecret)

    data class TrackerOptions(
        internal val request: TrackerRequest
    ) : Options(clientSecret = "")

    data class RetrieveAvailablePaymentMethodsOptions(
        override val clientSecret: String,
        /**
         * Page number starting from 0
         */
        internal val pageNum: Int,
        /**
         * Number of payment methods to be listed per page
         */
        internal val pageSize: Int,
        /**
         * Indicate whether the payment method type is active
         */
        internal val active: Boolean?,
        /**
         * The supported transaction currency
         */
        internal val transactionCurrency: String?,
        /**
         * The supported transaction mode. One of oneoff, recurring.
         */
        internal val transactionMode: TransactionMode?,
        /**
         * The supported country code
         */
        internal val countryCode: String?
    ) : Options(clientSecret = clientSecret)

    data class RetrievePaymentMethodTypeInfoOptions(
        override val clientSecret: String,
        /**
         * bank_transfer, online_banking etc.
         */
        internal val paymentMethodType: String,
        /**
         * webqr, mweb, jsapi, inapp, miniprog
         */
        internal val flow: AirwallexPaymentRequestFlow?,
        /**
         * recurring, oneoff
         */
        internal val transactionMode: TransactionMode?,
        /**
         * Country code
         */
        internal val countryCode: String?,
        /**
         * Open Id
         */
        internal val openId: String?
    ) : Options(clientSecret = clientSecret)

    data class RetrieveBankOptions(
        override val clientSecret: String,
        /**
         * bank_transfer, online_banking etc.
         */
        internal val paymentMethodType: String,
        /**
         * webqr, mweb, jsapi, inapp, miniprog
         */
        internal val flow: AirwallexPaymentRequestFlow?,
        /**
         * recurring, oneoff
         */
        internal val transactionMode: TransactionMode?,
        /**
         * For payment method like online_banking that supports different bank list in different country, the country code is required.
         * such payment method: online_banking, bank_transfer
         */
        internal val countryCode: String?,
        /**
         * Open Id
         */
        internal val openId: String?
    ) : Options(clientSecret = clientSecret)
}
