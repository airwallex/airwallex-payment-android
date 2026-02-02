package com.airwallex.android.view.composables.card

import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodTypeInfo

/**
 * Represents different payment operations
 */
sealed class PaymentOperation {
    /**
     * Operation for fetching available payment methods and consents
     * This is typically triggered automatically on screen initialization
     */
    data object FetchPaymentMethods : PaymentOperation()

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

    /**
     * Operation for checkout with Google Pay
     */
    data object CheckoutWithGooglePay : PaymentOperation()

    /**
     * Operation for direct payment with schema (no additional fields)
     *
     * @param paymentMethodType The payment method type to pay with
     */
    data class DirectPay(val paymentMethodType: AvailablePaymentMethodType) : PaymentOperation()

    /**
     * Operation for payment with schema fields
     *
     * @param paymentMethod The payment method
     * @param typeInfo Payment method type information
     * @param fieldMap Additional field data from the schema
     */
    data class PayWithFields(
        val paymentMethod: PaymentMethod,
        val typeInfo: PaymentMethodTypeInfo,
        val fieldMap: Map<String, String>
    ) : PaymentOperation()
}

/**
 * Represents the result of a payment operation
 */
sealed class PaymentOperationResult {
    /**
     * Result for the FetchPaymentMethods operation
     *
     * @param result The result containing available payment methods and consents, or failure
     */
    data class FetchPaymentMethods(
        val result: Result<Pair<List<AvailablePaymentMethodType>, List<PaymentConsent>>>
    ) : PaymentOperationResult()

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

    /**
     * Result for the CheckoutWithGooglePay operation
     *
     * @param status The payment status result (contains success or failure state)
     */
    data class CheckoutWithGooglePay(val status: AirwallexPaymentStatus) : PaymentOperationResult()

    /**
     * Result for the DirectPay operation
     *
     * @param status The payment status result (contains success or failure state)
     */
    data class DirectPay(val status: AirwallexPaymentStatus) : PaymentOperationResult()

    /**
     * Result for the PayWithFields operation
     *
     * @param status The payment status result (contains success or failure state)
     */
    data class PayWithFields(val status: AirwallexPaymentStatus) : PaymentOperationResult()

    /**
     * Result for any operation error
     *
     * @param message Error message to display to the user
     * @param exception Optional exception for debugging/logging
     */
    data class Error(val message: String, val exception: Throwable? = null) : PaymentOperationResult()
}
