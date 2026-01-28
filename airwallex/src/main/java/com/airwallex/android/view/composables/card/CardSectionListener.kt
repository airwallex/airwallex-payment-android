package com.airwallex.android.view.composables.card

import com.airwallex.android.core.AirwallexPaymentStatus

/**
 * Listener interface for CardSection events.
 * This interface provides Java-friendly callbacks for card operations.
 *
 * Usage from Java:
 * ```java
 * CardSection(
 *     viewModel,
 *     schemes,
 *     consents,
 *     false,
 *     new CardSectionListener() {
 *         @Override
 *         public void onLoadingChanged(@Nullable CardOperation operation) {
 *             if (operation instanceof CardOperation.AddCard) {
 *                 showLoading();
 *             } else {
 *                 hideLoading();
 *             }
 *         }
 *
 *         @Override
 *         public void onPaymentResult(@NonNull AirwallexPaymentStatus status) {
 *             // Handle result
 *         }
 *     }
 * );
 * ```
 */
interface CardSectionListener {
    /**
     * Called when the loading state changes for a card operation.
     *
     * @param operation The operation in progress, or null if no operation is running
     */
    fun onLoadingChanged(operation: CardOperation?)

    /**
     * Called when a payment operation completes.
     *
     * @param status The payment status result
     */
    fun onPaymentResult(status: AirwallexPaymentStatus)

    // Future: Add other callbacks as needed
    // fun onDeleteResult(result: Result<PaymentConsent>)
}
