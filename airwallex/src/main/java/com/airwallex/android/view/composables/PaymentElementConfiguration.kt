package com.airwallex.android.view.composables

import android.os.Parcelable
import com.airwallex.android.core.AirwallexSupportedCard
import com.airwallex.android.core.PaymentAppearance
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.google.pay.button.ButtonType
import kotlinx.parcelize.Parcelize

/**
 * Configuration for Airwallex Payment Element.
 *
 * Use [Card] for standalone card payment UI.
 * Use [PaymentSheet] for multi-payment method UI with Tab or Accordion layout.
 */
sealed class PaymentElementConfiguration {
    /**
     * Checkout button configuration
     */
    abstract val checkoutButton: CheckoutButton

    /**
     * Payment UI appearance configuration (theme color and dark mode).
     * When null, uses default appearance.
     */
    abstract val paymentAppearance: PaymentAppearance?

    /**
     * Google Pay button configuration.
     * Controls how the Google Pay button is displayed in the payment sheet.
     */
    @Parcelize
    data class GooglePayButton(
        /**
         * If true, Google Pay is shown as a standalone button on top of the payment sheet.
         * If false, Google Pay appears as a regular payment method option.
         */
        val showsAsPrimaryButton: Boolean = true,

        /**
         * The Google Pay button type (BUY, SUBSCRIBE, etc.).
         * When null, defaults to BUY for one-off payments, SUBSCRIBE for recurring payments.
         */
        val buttonType: ButtonType? = null
    ) : Parcelable

    /**
     * Checkout button configuration.
     * Controls the payment confirmation button appearance and text.
     */
    @Parcelize
    data class CheckoutButton(
        /**
         * Custom title for the checkout button.
         * When null, defaults to "Pay" for one-off payments, "Confirm" for recurring payments.
         */
        val title: String? = null
    ) : Parcelable

    /**
     * Configuration for standalone card payment element.
     * Shows only card input and saved cards (if available).
     *
     * @param supportedCardBrands List of supported card brands/schemes.
     *                            Defaults to all cards from [AirwallexSupportedCard]
     *                            (Visa, Amex, Mastercard, Discover, JCB, Diners Club, UnionPay).
     * @param checkoutButton Checkout button configuration
     * @param paymentAppearance Payment UI appearance configuration (theme color and dark mode)
     */
    @Parcelize
    data class Card(
        val supportedCardBrands: List<AirwallexSupportedCard> = enumValues<AirwallexSupportedCard>().toList(),
        override val checkoutButton: CheckoutButton = CheckoutButton(),
        override val paymentAppearance: PaymentAppearance? = null
    ) : PaymentElementConfiguration(), Parcelable

    /**
     * Configuration for payment sheet with multiple payment methods.
     * Can display in Tab or Accordion layout.
     *
     * @param layout The layout type - TAB or ACCORDION
     * @param googlePayButton Google Pay button configuration
     * @param checkoutButton Checkout button configuration
     * @param paymentAppearance Payment UI appearance configuration (theme color and dark mode)
     */
    @Parcelize
    data class PaymentSheet(
        val layout: PaymentMethodsLayoutType = PaymentMethodsLayoutType.TAB,
        val googlePayButton: GooglePayButton = GooglePayButton(),
        override val checkoutButton: CheckoutButton = CheckoutButton(),
        override val paymentAppearance: PaymentAppearance? = null
    ) : PaymentElementConfiguration(), Parcelable
}