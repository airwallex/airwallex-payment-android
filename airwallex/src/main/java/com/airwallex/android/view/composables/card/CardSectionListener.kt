package com.airwallex.android.view.composables.card

/**
 * Listener interface for CardSection events.
 * This interface provides Java-friendly callbacks for payment operations.
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
 *         public void onOperationStart(@NonNull PaymentOperation operation) {
 *             if (operation instanceof PaymentOperation.AddCard) {
 *                 showLoading();
 *             }
 *         }
 *
 *         @Override
 *         public void onOperationDone(@NonNull PaymentOperationResult result) {
 *             hideLoading();
 *             if (result instanceof PaymentOperationResult.AddCard) {
 *                 PaymentOperationResult.AddCard addCardResult =
 *                     (PaymentOperationResult.AddCard) result;
 *                 AirwallexPaymentStatus status = addCardResult.getStatus();
 *                 if (status instanceof AirwallexPaymentStatus.Success) {
 *                     handleSuccess((AirwallexPaymentStatus.Success) status);
 *                 } else if (status instanceof AirwallexPaymentStatus.Failure) {
 *                     handleFailure((AirwallexPaymentStatus.Failure) status);
 *                 }
 *             }
 *         }
 *     }
 * );
 * ```
 */
interface CardSectionListener {
    /**
     * Called when a payment operation starts.
     *
     * @param operation The operation that is starting
     */
    fun onOperationStart(operation: PaymentOperation)

    /**
     * Called when a payment operation completes (success or error).
     *
     * @param result The operation result containing success or error data
     */
    fun onOperationDone(result: PaymentOperationResult)
}
