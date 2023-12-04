package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AvailablePaymentConsentsResponse internal constructor(
    override val hasMore: Boolean,
    override val items: List<PaymentConsent>
) : Page<PaymentConsent>, AirwallexModel, Parcelable