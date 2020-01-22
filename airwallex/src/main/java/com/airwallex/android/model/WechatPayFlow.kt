package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WechatPayFlow constructor(

    @SerializedName("flow")
    val flow: WechatPayFlowType
) : AirwallexModel, Parcelable