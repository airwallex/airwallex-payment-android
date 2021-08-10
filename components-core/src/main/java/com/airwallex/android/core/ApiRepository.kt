package com.airwallex.android.core

import com.airwallex.android.core.model.*

/**
 * An interface for making Airwallex API requests
 */
interface ApiRepository {

    /**
     * Continue the [PaymentIntent] using [Options], used for 3DS
     *
     * @return a [PaymentIntent] from Airwallex server
     */
    fun continuePaymentIntent(
        options: Options
    ): PaymentIntent?

    /**
     * Confirm the [PaymentIntent] using [Options]
     *
     * @return a [PaymentIntent] from Airwallex server
     */
    fun confirmPaymentIntent(
        options: Options
    ): PaymentIntent?

    /**
     * Retrieve the [PaymentIntent] using [Options]
     *
     * @return a [PaymentIntent] from Airwallex server
     */
    fun retrievePaymentIntent(
        options: Options
    ): PaymentIntent?

    /**
     * Create a Airwallex [PaymentMethod] using [Options]
     *
     * @return a [PaymentMethod] from Airwallex server
     */
    fun createPaymentMethod(
        options: Options
    ): PaymentMethod?

    /**
     * Retrieve paRes with id
     */
    fun retrieveParesWithId(
        options: Options
    ): ThreeDSecurePares?

    /**
     * Create a PaymentConsent
     */
    fun createPaymentConsent(
        options: Options
    ): PaymentConsent?

    /**
     * Verify a PaymentConsent
     */
    fun verifyPaymentConsent(
        options: Options
    ): PaymentConsent?

    /**
     * Disable a PaymentConsent
     */
    fun disablePaymentConsent(
        options: Options
    ): PaymentConsent?

    /**
     * Retrieve a PaymentConsent
     */
    fun retrievePaymentConsent(
        options: Options
    ): PaymentConsent?

    /**
     * Tracker
     */
    fun tracker(
        options: Options
    )

    /**
     * Retrieve available payment method types
     */
    fun retrieveAvailablePaymentMethods(
        options: Options
    ): AvailablePaymentMethodResponse?
}
