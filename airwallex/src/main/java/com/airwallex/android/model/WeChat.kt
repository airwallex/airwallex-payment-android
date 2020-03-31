package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeChat internal constructor(
    val appId: String?,
    val partnerId: String?,
    val prepayId: String?,
    val packageValue: String?,
    val nonceStr: String?,
    val timestamp: String?,
    val sign: String?
) : AirwallexModel, Parcelable
