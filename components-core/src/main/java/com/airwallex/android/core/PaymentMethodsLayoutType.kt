package com.airwallex.android.core

import com.airwallex.android.core.log.AnalyticsLogger

enum class PaymentMethodsLayoutType {
    TAB,
    ACCORDION,
}

/**
 * Convert PaymentMethodsLayoutType to analytics layout string
 */
fun PaymentMethodsLayoutType.toAnalyticsLayoutString(): String = when (this) {
    PaymentMethodsLayoutType.TAB -> AnalyticsLogger.Layout.TAB
    PaymentMethodsLayoutType.ACCORDION -> AnalyticsLogger.Layout.ACCORDION
}