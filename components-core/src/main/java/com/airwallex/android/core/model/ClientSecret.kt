package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ClientSecret internal constructor(

    val value: String,

    val expiredTime: Date
) : AirwallexModel, Parcelable
