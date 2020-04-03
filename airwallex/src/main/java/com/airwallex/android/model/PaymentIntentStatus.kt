package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * The status of a [PaymentIntent]
 */
@Parcelize
enum class PaymentIntentStatus : Parcelable {

    /**
     * The payment was successful, no further action required.
     */
    @SerializedName("SUCCEEDED")
    SUCCEEDED,

    /**
     * The payment intent has been cancelled. Uncaptured funds will be returned.
     */
    @SerializedName("CANCELLED")
    CANCELLED,

    /**
     * Populate `payment_method` when calling confirm
     * This value is returned if `payment_method` is either null or the `payment_method` has failed during confirm, and a different `payment_method` should be provided.
     *
     */
    @SerializedName("REQUIRES_PAYMENT_METHOD")
    REQUIRES_PAYMENT_METHOD,

    /**
     * Pending customer action, see `next_action` for details. Possible causes are pending 3DS authentication, QR code scan.
     */
    @SerializedName("REQUIRES_CUSTOMER_ACTION")
    REQUIRES_CUSTOMER_ACTION,

    /**
     * See `next_action` for the details. For example `next_action=capture` indicates that capture is outstanding.
     */
    @SerializedName("REQUIRES_CAPTURE")
    REQUIRES_CAPTURE
}
