package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The type of [PaymentMethod]
 */
@Parcelize
enum class PaymentMethodType(
    val value: String,
) : Parcelable {

    CARD("card"),
    WECHAT("wechatpay"),
    REDIRECT("redirect");

    val dependencyName: String
        get() {
            return when (this) {
                CARD -> "payment-card"
                WECHAT -> "payment-wechat"
                else -> "payment-redirect"
            }
        }

    companion object {
        fun fromValue(value: String?): PaymentMethodType {
            return values().firstOrNull { it.value == value } ?: REDIRECT
        }
    }
}
