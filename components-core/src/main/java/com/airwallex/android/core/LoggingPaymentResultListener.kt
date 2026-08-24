package com.airwallex.android.core

import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.AnalyticsLogger.Field

/**
 * A [Airwallex.PaymentResultListener] wrapper that logs payment result events.
 * Used to ensure payment results are logged exactly once at public API entry points.
 */
internal class LoggingPaymentResultListener(
    private val delegate: Airwallex.PaymentResultListener,
    private val paymentMethod: String
) : Airwallex.PaymentResultListener {

    override fun onCompleted(status: AirwallexPaymentStatus) {
        when (status) {
            is AirwallexPaymentStatus.Success -> {
                AnalyticsLogger.logAction(
                    "payment_success",
                    mapOf(Field.PAYMENT_METHOD to paymentMethod)
                )
            }

            is AirwallexPaymentStatus.Cancel -> {
                AnalyticsLogger.logAction(
                    "payment_canceled",
                    mapOf(Field.PAYMENT_METHOD to paymentMethod)
                )
            }

            is AirwallexPaymentStatus.Failure -> {
                AnalyticsLogger.logAction(
                    "payment_failed",
                    mapOf(
                        Field.PAYMENT_METHOD to paymentMethod,
                        Field.MESSAGE to (status.exception.message ?: "")
                    )
                )
            }

            is AirwallexPaymentStatus.InProgress -> {
                AnalyticsLogger.logAction(
                    "payment_in_progress",
                    mapOf(Field.PAYMENT_METHOD to paymentMethod)
                )
            }
        }
        delegate.onCompleted(status)
    }

    companion object {
        /**
         * Wraps [listener] so payment result events are logged. If it is already a
         * [LoggingPaymentResultListener], returns it as-is to avoid double-logging.
         */
        fun wrap(
            listener: Airwallex.PaymentResultListener,
            paymentMethod: String
        ): Airwallex.PaymentResultListener {
            return listener as? LoggingPaymentResultListener
                ?: LoggingPaymentResultListener(listener, paymentMethod)
        }
    }
}
