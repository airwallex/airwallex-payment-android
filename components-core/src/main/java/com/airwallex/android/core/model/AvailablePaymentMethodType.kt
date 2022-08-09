package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AvailablePaymentMethodType internal constructor(

    /**
     * The name of payment method type.
     */
    val name: String,

    /**
     * The display name of payment method type.
     */
    val displayName: String? = null,

    /**
     * Indicate in which mode you trigger transactions with the payment method type. One of oneoff, recurring.
     */
    val transactionMode: TransactionMode? = null,

    /**
     * The supported flows for the payment method type and the transaction mode. A flow can be one of webqr, mweb, inapp
     */
    val flows: List<AirwallexPaymentRequestFlow>? = null,

    /**
     * The supported transaction currencies for the payment method type and the transaction mode
     */
    val transactionCurrencies: List<String>? = null,

    /**
     * The supported country codes for the payment method type and the transaction mode
     */
    val countryCodes: List<String>? = null,

    /**
     * Indicate whether the payment method type is active
     */
    val active: Boolean? = null,

    /**
     * The resources of payment method
     */
    val resources: AvailablePaymentMethodTypeResource? = null,

    /**
     * Supported card schemes.
     */
    val cardSchemes: List<CardScheme>? = null

) : AirwallexModel, Parcelable
