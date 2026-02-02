package com.airwallex.android.view.composables

import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.model.CardScheme

/**
 * Configuration for Airwallex Payment Element.
 *
 * Use [Card] for standalone card payment UI.
 * Use [PaymentSheet] for multi-payment method UI with Tab or Accordion layout.
 */
sealed class AwxPaymentElementConfiguration {
    /**
     * Configuration for standalone card payment element.
     * Shows only card input and saved cards (if available).
     *
     * @param cardSchemes List of supported card schemes. If empty, will be fetched automatically.
     */
    data class Card(val cardSchemes: List<CardScheme> = emptyList()) : AwxPaymentElementConfiguration()

    /**
     * Configuration for payment sheet with multiple payment methods.
     * Can display in Tab or Accordion layout.
     *
     * @param type The layout type - TAB or ACCORDION
     */
    data class PaymentSheet(
        val type: PaymentMethodsLayoutType = PaymentMethodsLayoutType.TAB
    ) : AwxPaymentElementConfiguration()
}
