package com.airwallex.android.core.model

/**
 * The params that used for retrieve [PaymentMethod]
 */
data class RetrieveAvailablePaymentMethodParams internal constructor(
    val clientSecret: String,
    /**
     * Page number starting from 0
     */
    val pageNum: Int,
    /**
     * Number of payment methods to be listed per page
     */
    val pageSize: Int,
    /**
     * Indicate whether the payment method type is active
     */
    val active: Boolean?,
    /**
     * The supported transaction currency
     */
    val transactionCurrency: String?,
    /**
     * The supported transaction mode. One of oneoff, recurring.
     */
    val transactionMode: TransactionMode?,
    /**
     * The supported country code
     */
    val countryCode: String?
) {

    class Builder(
        private val clientSecret: String,
        private val pageNum: Int
    ) : ObjectBuilder<RetrieveAvailablePaymentMethodParams> {

        private var pageSize: Int = 20
        private var active: Boolean? = null
        private var transactionCurrency: String? = null
        private var transactionMode: TransactionMode? = null
        private var countryCode: String? = null

        fun setPageSize(pageSize: Int): Builder = apply {
            this.pageSize = pageSize
        }

        fun setActive(active: Boolean?): Builder = apply {
            this.active = active
        }

        fun setTransactionCurrency(transactionCurrency: String?): Builder = apply {
            this.transactionCurrency = transactionCurrency
        }

        fun setTransactionMode(transactionMode: TransactionMode?): Builder = apply {
            this.transactionMode = transactionMode
        }

        fun setCountryCode(countryCode: String?): Builder = apply {
            this.countryCode = countryCode
        }

        override fun build(): RetrieveAvailablePaymentMethodParams {
            return RetrieveAvailablePaymentMethodParams(
                clientSecret = clientSecret,
                pageNum = pageNum,
                pageSize = pageSize,
                active = active,
                transactionCurrency = transactionCurrency,
                transactionMode = transactionMode,
                countryCode = countryCode
            )
        }
    }
}
