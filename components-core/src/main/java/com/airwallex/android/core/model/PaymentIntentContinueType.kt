package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class PaymentIntentContinueType(val value: String) : Parcelable {

    ENROLLMENT("3dsCheckEnrollment"),

    VALIDATE("3dsValidate"),

    DCC("dcc");

    internal companion object {
        internal fun fromValue(value: String?): PaymentIntentContinueType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
