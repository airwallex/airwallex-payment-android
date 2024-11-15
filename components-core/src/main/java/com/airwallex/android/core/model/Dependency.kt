package com.airwallex.android.core.model

enum class Dependency(val value: String) {
    CARD("payment-card"),
    WECHAT("payment-wechat"),
    REDIRECT("payment-redirect"),
    GOOGLEPAY("payment-googlepay");

    companion object {
        fun fromValue(value: String?): Dependency {
            return Dependency.values()
                .firstOrNull { it.value == value } ?: REDIRECT
        }
    }
}
