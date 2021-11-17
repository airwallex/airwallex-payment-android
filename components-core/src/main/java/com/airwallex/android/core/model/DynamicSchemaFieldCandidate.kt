package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DynamicSchemaFieldCandidate internal constructor(
    /**
     * Display Name
     */
    val displayName: String,

    /**
     * Value
     */
    val value: String
) : Parcelable {

    override fun toString(): String {
        return value
    }
}
