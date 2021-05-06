package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ThirdPartPayRequestFlow(val value: String) : Parcelable {

    IN_APP("inapp");

    internal companion object {
        internal fun fromValue(value: String?): ThirdPartPayRequestFlow? {
            return values().firstOrNull { it.value == value }
        }
    }
}
