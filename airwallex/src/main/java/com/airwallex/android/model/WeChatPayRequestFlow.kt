package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * RequestFlow for WeChatPay
 */
@Parcelize
enum class WeChatPayRequestFlow(val value: String) : Parcelable {

    IN_APP("inapp");

    internal companion object {
        internal fun fromValue(value: String?): WeChatPayRequestFlow? {
            return values().firstOrNull { it.value == value }
        }
    }
}
