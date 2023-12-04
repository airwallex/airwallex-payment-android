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
        options: Options.ContinuePaymentIntentOptions
    ): PaymentIntent?

    /**
     * Confirm the [PaymentIntent] using [Options]
     *
     * @return a [PaymentIntent] from Airwallex server
     */
    suspend fun confirmPaymentIntent(
        options: Options.ConfirmPaymentIntentOptions
    ): PaymentIntent?

    /**
     * Retrieve the [PaymentIntent] using [Options]
     *
     * @return a [PaymentIntent] from Airwallex server
     */
    suspend fun retrievePaymentIntent(
        options: Options.RetrievePaymentIntentOptions
    ): PaymentIntent?

    /**
     * Create a Airwallex [PaymentMethod] using [Options]
     *
     * @return a [PaymentMethod] from Airwallex server
     */
    suspend fun createPaymentMethod(
        options: Options.CreatePaymentMethodOptions
    ): PaymentMethod?

    /**
     * Create a PaymentConsent
     */
    suspend fun createPaymentConsent(
        options: Options.CreatePaymentConsentOptions
    ): PaymentConsent?

    /**
     * Verify a PaymentConsent
     */
    suspend fun verifyPaymentConsent(
        options: Options.VerifyPaymentConsentOptions
    ): PaymentConsent?

    /**
     * Disable a PaymentConsent
     */
    suspend fun disablePaymentConsent(
        options: Options.DisablePaymentConsentOptions
    ): PaymentConsent?

    /**
     * Retrieve a PaymentConsent
     */
    suspend fun retrievePaymentConsent(
        options: Options.RetrievePaymentConsentOptions
    ): PaymentConsent?

    /**
     * Retrieve available payment consents
     */
    suspend fun retrieveAvailablePaymentConsents(
        options: Options.RetrieveAvailablePaymentConsentsOptions
    ): Page<PaymentConsent>?

    /**
     * Tracker
     */
    suspend fun tracker(
        options: Options.TrackerOptions
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
        options: Options.RetrieveAvailablePaymentMethodsOptions
    ): Page<AvailablePaymentMethodType>?

    /**
     * Retrieve payment method detail
     */
    suspend fun retrievePaymentMethodTypeInfo(
        options: Options.RetrievePaymentMethodTypeInfoOptions
    ): PaymentMethodTypeInfo?

    /**
     * Retrieve banks of payment method
     */
    suspend fun retrieveBanks(
        options: Options.RetrieveBankOptions
    ): BankResponse?
}
