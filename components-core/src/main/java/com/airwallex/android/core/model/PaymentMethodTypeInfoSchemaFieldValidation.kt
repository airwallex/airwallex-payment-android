package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethodTypeInfoSchemaFieldValidation internal constructor(
    val regex: String? = null,

    val max: Int? = null
) : Parcelable
