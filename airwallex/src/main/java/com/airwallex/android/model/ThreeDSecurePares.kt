package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThreeDSecurePares internal constructor(

    val paresId: String,

    val pares: String
) : AirwallexModel, Parcelable
