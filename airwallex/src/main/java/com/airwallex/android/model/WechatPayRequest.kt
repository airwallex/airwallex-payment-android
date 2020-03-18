package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WechatPayRequest constructor(

    // The specific WeChat Pay flow to use. One of WechatPayFlowType
    @SerializedName("flow")
    val flow: WechatPayRequestFlow?

) : AirwallexModel, Parcelable
