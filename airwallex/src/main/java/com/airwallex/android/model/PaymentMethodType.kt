package com.airwallex.android.model

import com.google.gson.annotations.SerializedName

enum class PaymentMethodType(val code: String, val displayName: String) {

    @SerializedName("card")
    CARD("card", "Card"),

    @SerializedName("wechatpay")
    WECHAT("wechatpay", "Wechat pay")
}