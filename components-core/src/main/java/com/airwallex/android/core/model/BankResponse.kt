package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Response for retrieve available payment method types
 */
@Parcelize
data class BankResponse internal constructor(
    /**
     * Indicator that tells whether more data can be listed
     */
    val hasMore: Boolean,

    /**
     * List items
     */
    val items: List<Bank>?
) : AirwallexModel, Parcelable
