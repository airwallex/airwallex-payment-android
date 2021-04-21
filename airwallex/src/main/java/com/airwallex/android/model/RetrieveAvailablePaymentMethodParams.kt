package com.airwallex.android.model

/**
 * The params that used for retrieve [PaymentMethod]
 */
internal data class RetrieveAvailablePaymentMethodParams internal constructor(
    val clientSecret: String,
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
    internal val transactionMode: String?,
) {

    class Builder(
        private val clientSecret: String,
        private val pageNum: Int
    ) : ObjectBuilder<RetrieveAvailablePaymentMethodParams> {

        private var pageSize: Int = 20
        private var active: Boolean? = null
        private var transactionCurrency: String? = null
        private var transactionMode: String? = null

        fun setPageSize(pageSize: Int): Builder = apply {
            this.pageSize = pageSize
        }

        fun setActive(active: Boolean?): Builder = apply {
            this.active = active
        }

        fun setTransactionCurrency(transactionCurrency: String?): Builder = apply {
            this.transactionCurrency = transactionCurrency
        }

        fun setTransactionMode(transactionMode: String?): Builder = apply {
            this.transactionMode = transactionMode
        }

        override fun build(): RetrieveAvailablePaymentMethodParams {
            return RetrieveAvailablePaymentMethodParams(
                clientSecret = clientSecret,
                pageNum = pageNum,
                pageSize = pageSize,
                active = active,
                transactionCurrency = transactionCurrency,
                transactionMode = transactionMode
            )
        }
    }
}
