package com.airwallex.android.core.model

/**
 * The params that used for retrieve [PaymentMethodTypeInfo]
 */
data class RetrieveBankParams internal constructor(
    val clientSecret: String,
    internal val paymentMethodType: String,
    internal val countryCode: String?,
    internal val lang: String?
) {
    class Builder(
        private val clientSecret: String,
        private val paymentMethodType: String
    ) : ObjectBuilder<RetrieveBankParams> {

        private var countryCode: String? = null
        private var lang: String? = null

        fun setCountryCode(countryCode: String?): Builder = apply {
            this.countryCode = countryCode
        }

        fun setLang(lang: String?): Builder = apply {
            this.lang = lang
        }

        override fun build(): RetrieveBankParams {
            return RetrieveBankParams(
                clientSecret = clientSecret,
                paymentMethodType = paymentMethodType,
                countryCode = countryCode,
                lang = lang
            )
        }
    }
}
