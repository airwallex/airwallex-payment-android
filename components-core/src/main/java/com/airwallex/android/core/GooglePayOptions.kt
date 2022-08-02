package com.airwallex.android.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayOptions (
    /**
     * The Google Pay API may return cards on file on Google.com (PAN_ONLY) and/or a device token on
     * an Android device authenticated with a 3-D Secure cryptogram (CRYPTOGRAM_3DS).
     */
    val allowedCardAuthMethods: List<String>? = null,
    /**
     * Merchant identifier for Airwallex
     */
    val merchantId: String,
    /**
     * Set to false if you don't support prepaid cards. Default: The prepaid card class is supported
     * for the card networks specified.
     */
    val allowPrepaidCards: Boolean? = null,
    /**
     * Set to false if you don't support credit cards. Default: The credit card class is supported
     * for the card networks specified.
     */
    val allowCreditCards: Boolean? = null,
    /**
     * Set to true to request assuranceDetails. This object provides information about the
     * validation performed on the returned payment data.
     */
    val assuranceDetailsRequired: Boolean? = null,
    /**
     * Set to true if you require a billing address. A billing address should only be requested if
     * it's required to process the transaction.
     */
    val billingAddressRequired: Boolean? = null,
    /**
     * The expected fields returned if billingAddressRequired is set to true.
     */
    val billingAddressParameters: BillingAddressParameters? = null
): Parcelable

@Parcelize
data class BillingAddressParameters (
    val format: Format? = Format.MIN,
    val phoneNumberRequired: Boolean? = false
): Parcelable {
    enum class Format {
        MIN,
        FULL
    }
}