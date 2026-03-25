package com.airwallex.android.view.composables

import com.airwallex.android.core.AirwallexSupportedCard
import com.airwallex.android.core.PaymentMethodsLayoutType

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
     * @param supportedCardBrands List of supported card brands/schemes.
     *                            Defaults to all cards from [AirwallexSupportedCard]
     *                            (Visa, Amex, Mastercard, Discover, JCB, Diners Club, UnionPay).
     */
    data class Card(
        val supportedCardBrands: List<AirwallexSupportedCard> = enumValues<AirwallexSupportedCard>().toList()
    ) : PaymentElementConfiguration()

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