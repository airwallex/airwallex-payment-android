package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class PaymentMethodType(val type: String, val displayName: String) : Parcelable {

    @SerializedName("card")
    CARD("card", "Card"),

    @SerializedName("wechatpay")
    WECHAT("wechatpay", "Wechat pay")
}
