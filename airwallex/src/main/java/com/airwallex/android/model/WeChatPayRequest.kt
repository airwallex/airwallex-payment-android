package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Request for WeChatPay
 */
@Parcelize
data class WeChatPayRequest constructor(

    /**
     * The specific WeChat Pay flow to use.
     */
    @SerializedName("flow")
    val flow: WeChatPayRequestFlow?

) : AirwallexModel, Parcelable
