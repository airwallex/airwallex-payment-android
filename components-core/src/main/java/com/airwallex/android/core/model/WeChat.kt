package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * WeChat Pay Payments
 */
@Parcelize
data class WeChat(
    val appId: String?,
    val partnerId: String?,
    val prepayId: String?,
    val `package`: String?,
    val nonceStr: String?,
    val timestamp: String?,
    val sign: String?
) : AirwallexModel, Parcelable
