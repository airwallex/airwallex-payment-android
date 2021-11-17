package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class DynamicSchemaFieldType(val value: String) : Parcelable {
    STRING("string"),
    ENUM("enum"),
    BANKS("banks"),
    BOOLEAN("boolean");

    companion object {
        fun fromValue(value: String?): DynamicSchemaFieldType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
