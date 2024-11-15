package com.airwallex.android.core

enum class ActionComponentProviderType(val value: String) {
    CARD("card"),
    REDIRECT("redirect"),
    WECHATPAY("wechatpay"),
    GOOGLEPAY("googlepay");

    companion object {
        fun fromValue(value: String?): ActionComponentProviderType {
            return ActionComponentProviderType.values()
                .firstOrNull { it.value == value } ?: REDIRECT
        }
    }
}