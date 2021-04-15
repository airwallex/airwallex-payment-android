package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.*
import kotlinx.android.parcel.Parcelize

/**
 * An interface for making Airwallex API requests
 */
internal interface ApiRepository {

    @Parcelize
    open class Options internal constructor(
        internal open val clientSecret: String
    ) : Parcelable

    /**
     * Continue the [PaymentIntent] using [ApiRepository.Options], used for 3DS
     *
     * @return a [PaymentIntent] from Airwallex server
     */
    fun continuePaymentIntent(
        options: Options
    ): PaymentIntent?

    /**
     * Confirm the [PaymentIntent] using [ApiRepository.Options]
     *
     * @return a [PaymentIntent] from Airwallex server
     */
    fun confirmPaymentIntent(
        options: Options
    ): PaymentIntent?

    /**
     * Retrieve the [PaymentIntent] using [ApiRepository.Options]
     *
     * @return a [PaymentIntent] from Airwallex server
     */
    fun retrievePaymentIntent(
        options: Options
    ): PaymentIntent?

    /**
     * Create a Airwallex [PaymentMethod] using [ApiRepository.Options]
     *
     * @return a [PaymentMethod] from Airwallex server
     */
    fun createPaymentMethod(
        options: Options
    ): PaymentMethod?

    /**
     * Disable a Airwallex [PaymentMethod] using [ApiRepository.Options]
     *
     * @return a [PaymentMethod] from Airwallex server
     */
    fun disablePaymentMethod(
        options: Options
    ): PaymentMethod?

    /**
     * Retrieve all of the customer's [PaymentMethod] using [ApiRepository.Options]
     *
     * @return a [PaymentMethodResponse] from Airwallex server
     */
    fun retrievePaymentMethods(
        options: Options
    ): PaymentMethodResponse?

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
}
