package com.airwallex.android.view.composables.card

/**
 * Represents different card-related operations that can trigger loading states
 *
 * @property isLoading Whether the operation is currently loading (true) or finished (false)
 */
sealed class CardOperation(val isLoading: Boolean) {
    /**
     * Operation for adding a new card and processing payment
     *
     * @param isLoading Whether the add card operation is in progress
     */
    data class AddCard(val loading: Boolean) : CardOperation(loading)

    // TODO: Add other operations as needed
    // data class DeleteCard(val paymentConsent: PaymentConsent, val loading: Boolean) : CardOperation(loading)
    // data class CheckoutWithCvc(val paymentConsent: PaymentConsent, val cvc: String, val loading: Boolean) : CardOperation(loading)
    // data class CheckoutWithoutCvc(val paymentConsent: PaymentConsent, val loading: Boolean) : CardOperation(loading)
}
