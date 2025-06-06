package com.airwallex.android.core.model

import android.os.Parcelable
import com.airwallex.android.core.LogoResources
import kotlinx.parcelize.Parcelize

@Parcelize
data class AvailablePaymentMethodTypeResource(

    /**
     * Is there a schema of payment method
     */
    val hasSchema: Boolean? = null,

    /**
     * logos of payment method
     */
    val logos: LogoResources? = null

) : AirwallexModel, Parcelable
