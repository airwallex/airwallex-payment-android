package com.airwallex.android.model

import com.google.gson.annotations.SerializedName

enum class PaymentMethodType(val value: String) {

    @SerializedName("card")
    CARD("Card"),

    @SerializedName("wechatpay")
    WECHAT("Wechat pay")
}