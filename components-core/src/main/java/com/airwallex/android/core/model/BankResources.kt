package com.airwallex.android.core.model

import android.os.Parcelable
import com.airwallex.android.core.LogoResources
import kotlinx.parcelize.Parcelize

/**
 * Resources of bank
 */
@Parcelize
data class BankResources internal constructor(
    /**
     * logos of bank
     */
    val logos: LogoResources? = null
) : AirwallexModel, Parcelable
