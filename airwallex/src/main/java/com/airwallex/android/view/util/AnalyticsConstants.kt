package com.airwallex.android.view.util

object AnalyticsConstants {
    // event names
    const val EVENT_PAYMENT_CANCELLED = "payment_canceled"
    const val EVENT_PAYMENT_LAUNCHED = "payment_launched"
    const val PAYMENT_SELECT = "select_payment"
    const val TAP_PAY_BUTTON = "tap_pay_button"
    const val CLICK_PAY_BUTTON = "click_pay_button"

    // page names
    const val CARD_PAYMENT_VIEW: String = "card_payment_view"
    const val PAGE_CREATE_CARD: String = "page_create_card"

    // extras
    const val SUPPORTED_SCHEMES: String = "supportedSchemes"
    const val PAYMENT_METHOD = "payment_method"
}