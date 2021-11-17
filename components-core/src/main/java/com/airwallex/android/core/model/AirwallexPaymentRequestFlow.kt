package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AirwallexPaymentRequestFlow(val value: String) : Parcelable {

    IN_APP("inapp"),
    M_WEB("mweb");

    companion object {
        fun fromValue(value: String?): AirwallexPaymentRequestFlow? {
            return values().firstOrNull { it.value == value }
        }
    }
}
