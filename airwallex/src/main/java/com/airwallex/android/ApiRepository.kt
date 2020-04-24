package com.airwallex.android

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * An interface for making Airwallex API requests
 */
internal interface ApiRepository {

    @Parcelize
    open class Options internal constructor(
        internal open val clientSecret: String
    ) : Parcelable

    fun confirmPaymentIntent(
        options: Options
    ): AirwallexHttpResponse?

    fun retrievePaymentIntent(
        options: Options
    ): AirwallexHttpResponse?
}
