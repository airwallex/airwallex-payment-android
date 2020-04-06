package com.airwallex.android

import com.airwallex.android.model.ObjectBuilder

data class RetrievePaymentMethodParams internal constructor(
    override val customerId: String,
    override val clientSecret: String,
    /**
     * Page number starting from 0
     */
    internal val pageNum: Int,
    /**
     * Number of payment methods to be listed per page
     */
    internal val pageSize: Int
) : AbstractPaymentMethodParams(customerId = customerId, clientSecret = clientSecret) {

    class Builder(
        private val customerId: String,
        private val clientSecret: String,
        private val pageNum: Int
    ) : ObjectBuilder<RetrievePaymentMethodParams> {

        private var pageSize: Int = 20

        fun setPageSize(pageSize: Int): Builder = apply {
            this.pageSize = pageSize
        }

        override fun build(): RetrievePaymentMethodParams {
            return RetrievePaymentMethodParams(
                customerId = customerId,
                clientSecret = clientSecret,
                pageNum = pageNum,
                pageSize = pageSize
            )
        }
    }
}
