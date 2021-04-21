package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Response for retrieve available payment method types
 */
@Parcelize
data class AvailablePaymentMethodResponse internal constructor(
    /**
     * Indicator that tells whether more data can be listed
     */
    val hasMore: Boolean,

    /**
     * List items
     */
    val items: List<AvailablePaymentMethod>?
) : AirwallexModel, Parcelable
