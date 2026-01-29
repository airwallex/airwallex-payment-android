package com.airwallex.android.view.composables.card

import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.model.PaymentConsent

/**
 * Represents different payment operations
 */
sealed class PaymentOperation {
    /**
     * Operation for adding a new card and processing payment
     */
    data object AddCard : PaymentOperation()

    /**
     * Operation for deleting a card
     *
     * @param loading Whether the delete operation is in progress
     * @param deletedConsent The deleted consent when operation succeeds (null during loading or on failure)
     * @param error Error message when operation fails (null during loading or on success)
     */
    data class DeleteCard(
        val loading: Boolean,
        val deletedConsent: PaymentConsent? = null,
        val error: String? = null
    ) : PaymentOperation()

    // TODO: Add other operations as needed
    // data object CheckoutWithCvc : PaymentOperation()
    // data object CheckoutWithoutCvc : PaymentOperation()
}

/**
 * Represents the result of a payment operation
 */
sealed class PaymentOperationResult {
    /**
     * Result for the AddCard operation
     *
     * @param status The payment status result (contains success or failure state)
     */
    data class AddCard(val status: AirwallexPaymentStatus) : PaymentOperationResult()

    // TODO: Add other operation results as needed
    // data class CheckoutWithCvc(val status: AirwallexPaymentStatus) : PaymentOperationResult()
}
