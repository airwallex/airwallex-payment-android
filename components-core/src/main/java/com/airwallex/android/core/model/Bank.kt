package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Bank information.
 */
@Parcelize
data class Bank internal constructor(
    /**
     * name of the bank
     */
    val name: String,

    /**
     * display name of the bank
     */
    val displayName: String,

    /**
     * logo of the bank
     */
    val resources: BankResources?

) : AirwallexModel, Parcelable
