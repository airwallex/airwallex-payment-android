package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class DynamicSchemaFieldUIType(val value: String) : Parcelable {
    TEXT("text"), // string
    EMAIL("email"), // string
    PHONE("phone"), // string
    LIST("list"), // enum
    LOGO_LIST("logo_list"), // banks
    CHECKBOX("checkbox"); // boolean

    companion object {
        fun fromValue(value: String?): DynamicSchemaFieldUIType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
