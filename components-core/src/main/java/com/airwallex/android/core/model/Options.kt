package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class Options constructor(
    open val clientSecret: String
) : Parcelable
