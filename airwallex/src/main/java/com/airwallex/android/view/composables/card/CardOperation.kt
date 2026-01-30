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
    data class DeleteCard(val deletedConsent: PaymentConsent? = null) : PaymentOperation()

    /**
     * Operation for checkout with CVC
     *
     * @param consent The payment consent to checkout with
     * @param cvc The CVC code
     */
    data class CheckoutWithCvc(val consent: PaymentConsent, val cvc: String) : PaymentOperation()

    /**
     * Operation for checkout without CVC
     *
     * @param consent The payment consent to checkout with
     */
    data class CheckoutWithoutCvc(val consent: PaymentConsent) : PaymentOperation()
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
    data class DeleteCard(val result: Result<PaymentConsent>) : PaymentOperationResult()

    /**
     * Result for the CheckoutWithCvc operation
     *
     * @param status The payment status result (contains success or failure state)
     */
    data class CheckoutWithCvc(val status: AirwallexPaymentStatus) : PaymentOperationResult()

    /**
     * Result for the CheckoutWithoutCvc operation
     *
     * @param status The payment status result (contains success or failure state)
     */
    data class CheckoutWithoutCvc(val status: AirwallexPaymentStatus) : PaymentOperationResult()
}
