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
    suspend fun continuePaymentIntent(
        options: Options
    ): PaymentIntent?

    /**
     * Confirm the [PaymentIntent] using [Options]
     *
     * @return a [PaymentIntent] from Airwallex server
     */
    suspend fun confirmPaymentIntent(
        options: Options
    ): PaymentIntent?

    /**
     * Retrieve the [PaymentIntent] using [Options]
     *
     * @return a [PaymentIntent] from Airwallex server
     */
    suspend fun retrievePaymentIntent(
        options: Options
    ): PaymentIntent?

    /**
     * Create a Airwallex [PaymentMethod] using [Options]
     *
     * @return a [PaymentMethod] from Airwallex server
     */
    suspend fun createPaymentMethod(
        options: Options
    ): PaymentMethod?

    /**
     * Retrieve paRes with id
     */
    suspend fun retrieveParesWithId(
        options: Options
    ): ThreeDSecurePares?

    /**
     * Create a PaymentConsent
     */
    suspend fun createPaymentConsent(
        options: Options
    ): PaymentConsent?

    /**
     * Verify a PaymentConsent
     */
    suspend fun verifyPaymentConsent(
        options: Options
    ): PaymentConsent?

    /**
     * Disable a PaymentConsent
     */
    suspend fun disablePaymentConsent(
        options: Options
    ): PaymentConsent?

    /**
     * Retrieve a PaymentConsent
     */
    suspend fun retrievePaymentConsent(
        options: Options
    ): PaymentConsent?

    /**
     * Tracker
     */
    suspend fun tracker(
        options: Options
    )

    /**
     * Execute mock wechat (Just for demo env)
     */
    suspend fun executeMockWeChat(
        mockWeChatUrl: String
    )

    /**
     * Retrieve available payment method types
     */
    suspend fun retrieveAvailablePaymentMethods(
        options: Options
    ): AvailablePaymentMethodResponse?
}
