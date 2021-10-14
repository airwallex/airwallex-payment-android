package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethodTypeInfoSchemaField internal constructor(

    /**
     * Name of schema field
     */
    val name: String,

    /**
     * Display name of schema field
     */
    val displayName: String,

    /**
     * UI type of schema field, include text, email, phone, list, logo_list
     */
    val uiType: PaymentMethodTypeInfoSchemaFieldUIType? = null,

    /**
     * The type of schema field
     */
    val type: String? = null,

    /**
     * If need to hide
     */
    val hidden: Boolean,

    /**
     * Validations of schema field
     */
    val validations: PaymentMethodTypeInfoSchemaFieldValidation? = null
) : AirwallexModel, Parcelable {

    companion object {
        const val BANK_NAME = "bank_name"
    }
}
