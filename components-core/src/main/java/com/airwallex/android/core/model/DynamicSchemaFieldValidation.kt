package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DynamicSchemaFieldValidation internal constructor(
    val regex: String? = null,

    val max: Int? = null
) : Parcelable
