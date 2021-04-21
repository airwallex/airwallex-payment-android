package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.AvailablePaymentMethodParser
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AvailablePaymentMethod internal constructor(

    /**
     * The name of payment method type. One of card, wechatpay, alipaycn, alipayhk, kakaopay, tng, truemoney, dana, gcash.
     */
    val name: String? = null,

    /**
     * Indicate in which mode you trigger transactions with the payment method type. One of oneoff, recurring.
     */
    val transactionMode: TransactionMode? = null,

    /**
     *
     The supported flows for the payment method type and the transaction mode. A flow can be one of webqr, mweb, inapp
     */
    val flows: List<String>? = null,

    /**
     * The supported transaction currencies for the payment method type and the transaction mode
     */
    val transactionCurrencies: List<String>? = null,

    /**
     * Indicate whether the payment method type is active
     */
    val active: Boolean? = null,

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                name?.let {
                    mapOf(AvailablePaymentMethodParser.FIELD_NAME to it)
                }.orEmpty()
            )
            .plus(
                transactionMode?.let {
                    mapOf(AvailablePaymentMethodParser.FIELD_TRANSACTION_MODE to it.value)
                }.orEmpty()
            )
            .plus(
                flows?.let {
                    mapOf(AvailablePaymentMethodParser.FIELD_FLOWS to it)
                }.orEmpty()
            )
            .plus(
                transactionCurrencies?.let {
                    mapOf(AvailablePaymentMethodParser.FIELD_TRANSACTION_CURRENCIES to it)
                }.orEmpty()
            )
            .plus(
                active?.let {
                    mapOf(AvailablePaymentMethodParser.FIELD_ACTIVE to it)
                }.orEmpty()
            )
    }

    enum class TransactionMode(val value: String) {
        ONE_OFF("oneoff"), RECURRING("recurring");

        internal companion object {
            internal fun fromValue(value: String?): TransactionMode? {
                return values().firstOrNull { it.value == value }
            }
        }
    }
}
