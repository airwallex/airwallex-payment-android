package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * WeChat Pay Payments
 */
@Parcelize
data class WeChat internal constructor(
    val appId: String?,
    val partnerId: String?,
    val prepayId: String?,
    val `package`: String?,
    val nonceStr: String?,
    val timestamp: String?,
    val sign: String?
) : AirwallexModel, Parcelable
