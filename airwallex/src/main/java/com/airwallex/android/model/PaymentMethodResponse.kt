package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Response for retrieve payment methods
 */
@Parcelize
data class PaymentMethodResponse internal constructor(
    /**
     * Indicator that tells whether more data can be listed
     */
    @SerializedName("has_more")
    val hasMore: Boolean,

    /**
     * List items
     */
    @SerializedName("items")
    val items: List<PaymentMethod>
) : AirwallexModel, Parcelable
