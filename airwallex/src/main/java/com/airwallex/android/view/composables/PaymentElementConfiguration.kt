package com.airwallex.android.view.composables

import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.model.CardScheme

/**
 * Configuration for Airwallex Payment Element.
 *
 * Use [Card] for standalone card payment UI.
 * Use [PaymentSheet] for multi-payment method UI with Tab or Accordion layout.
 */
sealed class PaymentElementConfiguration {
    /**
     * Configuration for standalone card payment element.
     * Shows only card input and saved cards (if available).
     *
     * @param supportedCardBrands List of supported card brands/schemes. If empty, will be fetched automatically.
     */
    data class Card(val supportedCardBrands: List<CardScheme> = emptyList()) : PaymentElementConfiguration()

    /**
     * Configuration for payment sheet with multiple payment methods.
     * Can display in Tab or Accordion layout.
     *
     * @param layout The layout type - TAB or ACCORDION
     */
    data class PaymentSheet(
        val layout: PaymentMethodsLayoutType = PaymentMethodsLayoutType.TAB,
        val showsGooglePayAsPrimaryButton: Boolean = true,
    ) : PaymentElementConfiguration()
}