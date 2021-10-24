package com.airwallex.android.core.model

import android.os.Parcelable
import com.airwallex.android.core.LogoResources
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethodTypeInfo internal constructor(

    /**
     * Name of the payment method
     */
    val name: String? = null,

    /**
     * Display name of the payment method
     */
    val displayName: String? = null,

    /**
     * Logos of the payment method, include png & svg
     */
    val logos: LogoResources? = null,

    /**
     * Check if the payment method have schema fields
     */
    val hasSchema: Boolean? = null,

    /**
     * The detail required schema fields
     */
    val fieldSchemas: List<DynamicSchema>? = null
) : AirwallexModel, Parcelable
