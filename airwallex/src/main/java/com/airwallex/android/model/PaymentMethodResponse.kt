package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Response for retrieve payment methods
 */
@Parcelize
data class PaymentMethodResponse internal constructor(
    /**
     * Indicator that tells whether more data can be listed
     */
    val hasMore: Boolean,

    /**
     * List items
     */
    val items: List<PaymentMethod>
) : AirwallexModel, Parcelable
