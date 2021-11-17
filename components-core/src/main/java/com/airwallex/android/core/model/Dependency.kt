package com.airwallex.android.core.model

enum class Dependency(val value: String) {
    CARD("payment-card"),
    WECHAT("payment-wechat"),
    REDIRECT("payment-redirect")
}
