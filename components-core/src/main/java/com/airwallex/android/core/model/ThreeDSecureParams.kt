package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThreeDSecureParams internal constructor(

    val paresId: String,

    val pares: String
) : AirwallexModel, Parcelable
