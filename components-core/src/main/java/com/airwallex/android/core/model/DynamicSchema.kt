package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DynamicSchema internal constructor(
    /**
     * The supported transaction mode. One of oneoff, recurring.
     */
    val transactionMode: TransactionMode? = null,

    /**
     * Required payment method schema field
     */
    val fields: List<DynamicSchemaField>? = null
) : AirwallexModel, Parcelable
