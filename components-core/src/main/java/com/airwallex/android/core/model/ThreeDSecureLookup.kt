package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThreeDSecureLookup constructor(
    val transactionId: String?,
    val payload: String?,
    val acsUrl: String?,
    val version: String
) : Parcelable
