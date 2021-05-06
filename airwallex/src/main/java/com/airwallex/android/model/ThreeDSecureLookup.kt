package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThreeDSecureLookup internal constructor(
    val transactionId: String?,
    val payload: String?,
    val acsUrl: String?,
    val version: String
) : Parcelable
