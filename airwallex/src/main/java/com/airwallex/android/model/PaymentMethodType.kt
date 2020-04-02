package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * The type of [PaymentMethod]
 */
@Parcelize
enum class PaymentMethodType(val value: String) : Parcelable {

    @SerializedName("card")
    CARD("card"),

    @SerializedName("wechatpay")
    WECHAT("wechatpay")
}
