package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The type of [PaymentMethod]
 */
@Parcelize
enum class AvaliablePaymentMethodType(val value: String) : Parcelable {

    CARD("card"),
    ALIPAY_CN("alipaycn"),
    ALIPAY_HK("alipayhk"),
    WECHAT("wechatpay"),
    DANA("dana"),
    GCASH("gcash"),
    KAKAO("kakaopay"),
    TNG("tng");

    internal companion object {
        internal fun fromValue(value: String?): AvaliablePaymentMethodType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
