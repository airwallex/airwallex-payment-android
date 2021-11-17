package com.airwallex.android.core.model

enum class TransactionMode(val value: String) {
    ONE_OFF("oneoff"), RECURRING("recurring");

    internal companion object {
        internal fun fromValue(value: String?): TransactionMode? {
            return values().firstOrNull { it.value == value }
        }
    }
}
