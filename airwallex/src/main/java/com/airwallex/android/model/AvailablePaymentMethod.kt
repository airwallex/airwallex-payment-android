package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AvailablePaymentMethod internal constructor(

    /**
     * The name of payment method type. One of card, wechatpay, alipaycn, alipayhk, kakaopay, tng, truemoney, dana, gcash.
     */
    val name: PaymentMethodType? = null,

    /**
     * Indicate in which mode you trigger transactions with the payment method type. One of oneoff, recurring.
     */
    val transactionMode: TransactionMode? = null,

    /**
     *
     The supported flows for the payment method type and the transaction mode. A flow can be one of webqr, mweb, inapp
     */
    val flows: List<RedirectRequestFlow>? = null,

    /**
     * The supported transaction currencies for the payment method type and the transaction mode
     */
    val transactionCurrencies: List<String>? = null,

    /**
     * Indicate whether the payment method type is active
     */
    val active: Boolean? = null,

) : AirwallexModel, Parcelable {

    enum class TransactionMode(val value: String) {
        ONE_OFF("oneoff"), RECURRING("recurring");

        internal companion object {
            internal fun fromValue(value: String?): TransactionMode? {
                return values().firstOrNull { it.value == value }
            }
        }
    }
}
