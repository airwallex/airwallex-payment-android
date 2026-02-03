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
     * @param consentId The ID of the payment consent to delete
     */
    data class DeleteCard(val consentId: String) : PaymentOperation()

    /**
     * Operation for checkout with CVC
     *
     * @param consentId The ID of the payment consent to checkout with
     * @param paymentMethodType The payment method type for analytics (non-sensitive)
     */
    data class CheckoutWithCvc(
        val consentId: String,
        val paymentMethodType: String? = null
    ) : PaymentOperation()

    /**
     * Operation for checkout without CVC
     *
     * @param consentId The ID of the payment consent to checkout with
     * @param paymentMethodType The payment method type for analytics (non-sensitive)
     */
    data class CheckoutWithoutCvc(
        val consentId: String,
        val paymentMethodType: String? = null
    ) : PaymentOperation()

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

    /**
     * Operation for loading schema fields
     */
    data object LoadSchemaFields : PaymentOperation()
}

/**
 * Represents the result of a payment operation
 */
sealed class PaymentOperationResult {
    /**
     * Result for the FetchPaymentMethods operation
     * Data is already available in the ViewModel state flows (availablePaymentMethods, availablePaymentConsents)
     */
    data object FetchPaymentMethods : PaymentOperationResult()

    /**
     * Result for the AddCard operation
     *
     * @param status The payment status result (contains success or failure state)
     */
    data class AddCard(val status: AirwallexPaymentStatus) : PaymentOperationResult()

    /**
     * Result for the DeleteCard operation
     * PCI-DSS Compliant: Only returns the consent ID, not the full consent object
     *
     * @param deletedConsentId The ID of the payment consent that was successfully deleted
     */
    data class DeleteCard(val deletedConsentId: String) : PaymentOperationResult()

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
     * Result for the LoadSchemaFields operation
     */
    data object LoadSchemaFields : PaymentOperationResult()

    /**
     * Result for any operation error
     *
     * @param message Error message to display to the user
     * @param exception Optional exception for debugging/logging
     */
    data class Error(val message: String, val exception: Throwable? = null) : PaymentOperationResult()
}
