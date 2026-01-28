package com.airwallex.android.view.composables.card

/**
 * Represents different card-related operations that can trigger loading states
 */
sealed class CardOperation {
    /**
     * Operation for adding a new card and processing payment
     */
    object AddCard : CardOperation()

    // TODO: Add other operations as needed
    // data class DeleteCard(val paymentConsent: PaymentConsent) : CardOperation()
    // data class CheckoutWithCvc(val paymentConsent: PaymentConsent, cvc: String) : CardOperation()
    // data class CheckoutWithoutCvc(val paymentConsent: PaymentConsent) : CardOperation()
}
