package com.airwallex.android.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LogoResources internal constructor(
    /**
     * png of the logo
     */
    val png: String?,

    /**
     * svg of the logo
     */
    val svg: String?

) : Parcelable
