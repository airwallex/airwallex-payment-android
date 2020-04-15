package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * RequestFlow for WeChatPay
 */
@Parcelize
enum class WeChatPayRequestFlow : Parcelable {

    @SerializedName("inapp")
    IN_APP
}
