package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class PaymentMethodTypeInfoSchemaFieldUIType(val value: String) : Parcelable {
    TEXT("text"),
    EMAIL("email"),
    PHONE("phone"),
    LIST("list"),
    LOGO_LIST("logo_list");

    companion object {
        fun fromValue(value: String?): PaymentMethodTypeInfoSchemaFieldUIType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
