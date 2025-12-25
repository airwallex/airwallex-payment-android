package com.airwallex.paymentacceptance.util

import com.airwallex.android.core.model.PaymentIntent

/**
 * Extension to get description from PaymentAttempt, mirroring iOS implementation.
 * Returns failure details description if available, otherwise returns a user-friendly status description.
 */
fun PaymentIntent.PaymentAttempt.getDescription(): String {
    // If there are failure details, show them (matching iOS behavior)
    failureDetails?.let { return it.getDescription() }

    // Otherwise, show status description
    return when (status?.uppercase()) {
        "AUTHORIZED", "CAPTURE_REQUESTED", "REQUESTED_CAPTURE", "SETTLED", "PAID" -> "SUCCEED"
        else -> status ?: "UNKNOWN"
    }
}

fun PaymentIntent.FailureDetails.getDescription(): String {
    val code = details?.originalResponseCode ?: code
    val message = details?.originalResponseMessage ?: message
    return "$code\n$message"
}

/**
 * Check if the payment attempt has reached a final status.
 * Final statuses include both successful and failed end states.
 * Mirrors iOS implementation.
 */
fun PaymentIntent.PaymentAttempt.isFinal(): Boolean {
    // If there are failure details, it's final (matching iOS behavior)
    if (failureDetails != null) return true

    // Check if status is one of the final statuses
    return status?.uppercase() in listOf(
        "AUTHORIZED", "CAPTURE_REQUESTED", "REQUESTED_CAPTURE", "EXPIRED",
        "CANCELLED", "FAILED", "SETTLED", "PAID"
    )
}
