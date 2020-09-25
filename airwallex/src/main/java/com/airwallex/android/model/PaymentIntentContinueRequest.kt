package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * The request params to confirm [PaymentIntent]
 */
@Parcelize
data class PaymentIntentContinueRequest internal constructor(

    /**
     * Unique request ID specified by the merchant
     */
    @SerializedName("request_id")
    val requestId: String,

    /**
     * 3D Secure Type
     */
    @SerializedName("type")
    val type: PaymentIntentContinueType,

    /**
     * 3D Secure
     */
    @SerializedName("three_ds")
    val threeDSecure: PaymentMethodOptions.CardOptions.ThreeDSecure? = null,

    @SerializedName("device")
    val device: Device? = null,

    @SerializedName("use_dcc")
    val useDcc: Boolean? = null
) : AirwallexModel, Parcelable