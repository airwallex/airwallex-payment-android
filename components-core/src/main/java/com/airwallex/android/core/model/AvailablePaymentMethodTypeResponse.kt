package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Response for retrieve available payment method types
 */
@Parcelize
data class AvailablePaymentMethodTypeResponse internal constructor(
    /**
     * Indicator that tells whether more data can be listed
     */
    override val hasMore: Boolean,

    /**
     * List items
     */
    override val items: List<AvailablePaymentMethodType>
) : Page<AvailablePaymentMethodType>, AirwallexModel, Parcelable
