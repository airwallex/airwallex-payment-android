package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * The type of [PaymentMethod]
 */
@Parcelize
enum class PaymentMethodType(val value: String) : Parcelable {

    CARD("card"),

    WECHAT("wechatpay");

    internal companion object {
        internal fun fromValue(value: String?): PaymentMethodType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
