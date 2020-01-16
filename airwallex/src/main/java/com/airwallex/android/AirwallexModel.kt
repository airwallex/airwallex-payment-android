package com.airwallex.android

import android.os.Parcelable

/**
 * Model for a Stripe API object.
 */
interface AirwallexModel {
    override fun hashCode(): Int

    override fun equals(other: Any?): Boolean
}
