package com.airwallex.android.core.model

import com.airwallex.android.core.LocaleValidator
import java.util.Locale

/**
 * The params that used for retrieve [PaymentMethodTypeInfo]
 */
data class RetrieveBankParams internal constructor(
    val clientSecret: String,
    internal val paymentMethodType: String,
    internal val flow: AirwallexPaymentRequestFlow?,
    internal val transactionMode: TransactionMode?,
    internal val countryCode: String?,
    internal val openId: String?,
    internal val locale: Locale?
) {
    class Builder(
        private val clientSecret: String,
        private val paymentMethodType: String
    ) : ObjectBuilder<RetrieveBankParams> {

        private var flow: AirwallexPaymentRequestFlow? = null
        private var transactionMode: TransactionMode? = null
        private var countryCode: String? = null
        private var openId: String? = null
        private var locale: Locale? = null

        fun setFlow(flow: AirwallexPaymentRequestFlow?): Builder = apply {
            this.flow = flow
        }

        fun setTransactionMode(transactionMode: TransactionMode?): Builder = apply {
            this.transactionMode = transactionMode
        }

        fun setCountryCode(countryCode: String?): Builder = apply {
            this.countryCode = countryCode
        }

        fun setOpenId(openId: String?): Builder = apply {
            this.openId = openId
        }

        fun setLocale(locale: Locale?): Builder = apply {
            this.locale = locale
        }

        override fun build(): RetrieveBankParams {
            val validatedLocale = LocaleValidator.validatedOrNull(locale)
            return RetrieveBankParams(
                clientSecret = clientSecret,
                paymentMethodType = paymentMethodType,
                flow = flow,
                transactionMode = transactionMode,
                countryCode = countryCode,
                openId = openId,
                locale = validatedLocale
            )
        }
    }
}
