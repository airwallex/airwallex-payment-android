package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class WechatPayRequestFlow : Parcelable {

    @SerializedName("inapp")
    INAPP
}
