package com.airwallex.android

import android.os.Parcelable
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
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
     * @return a [AirwallexHttpResponse] from Airwallex server
     */
    fun continuePaymentIntent(
        options: Options
    ): AirwallexHttpResponse?

    /**
     * Confirm the [PaymentIntent] using [ApiRepository.Options]
     *
     * @return a [AirwallexHttpResponse] from Airwallex server
     */
    fun confirmPaymentIntent(
        options: Options
    ): AirwallexHttpResponse?

    /**
     * Retrieve the [PaymentIntent] using [ApiRepository.Options]
     *
     * @return a [AirwallexHttpResponse] from Airwallex server
     */
    fun retrievePaymentIntent(
        options: Options
    ): AirwallexHttpResponse?

    /**
     * Create a Airwallex [PaymentMethod] using [ApiRepository.Options]
     *
     * @return a [AirwallexHttpResponse] from Airwallex server
     */
    fun createPaymentMethod(
        options: Options
    ): AirwallexHttpResponse?

    /**
     * Retrieve all of the customer's [PaymentMethod] using [ApiRepository.Options]
     *
     * @return a [AirwallexHttpResponse] from Airwallex server
     */
    fun retrievePaymentMethods(
        options: Options
    ): AirwallexHttpResponse?

    /**
     * Retrieve paRes with id
     */
    fun retrieveParesWithId(
        options: Options
    ): AirwallexHttpResponse?
}
