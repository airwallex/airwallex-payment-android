package com.airwallex.android.view.util

import androidx.annotation.StringRes
import com.airwallex.android.R

/**
 * Maps an ISO country code to the appropriate billing address field label.
 *
 */
internal object BillingAddressLabels {

    @StringRes
    fun stateLabel(countryCode: String): Int = when (countryCode.uppercase()) {
        "CN" -> R.string.airwallex_billing_label_province
        "JP" -> R.string.airwallex_billing_label_prefecture
        "KR" -> R.string.airwallex_billing_label_do_si
        "IE", "TW" -> R.string.airwallex_billing_label_county
        "BB", "JM" -> R.string.airwallex_billing_label_parish
        "CO", "HN", "NI" -> R.string.airwallex_billing_label_department
        "NR" -> R.string.airwallex_billing_label_district
        "AE" -> R.string.airwallex_billing_label_emirate
        "RU", "UA" -> R.string.airwallex_billing_label_oblast
        "HK" -> R.string.airwallex_billing_label_area
        "BS", "CV", "KI", "KN", "KY", "PF", "SC", "TV" -> R.string.airwallex_billing_label_island
        else -> R.string.airwallex_billing_label_state
    }

    @StringRes
    fun cityLabel(countryCode: String): Int = when (countryCode.uppercase()) {
        "GB", "NO", "SE", "SJ" -> R.string.airwallex_billing_label_town
        "HK", "PE", "TR" -> R.string.airwallex_billing_label_district
        "AU" -> R.string.airwallex_billing_label_suburb
        else -> R.string.airwallex_billing_label_city
    }

    @StringRes
    fun postcodeLabel(countryCode: String): Int = when (countryCode.uppercase()) {
        "US", "GU", "PR" -> R.string.airwallex_billing_label_zip_code
        "IN" -> R.string.airwallex_billing_label_pin
        "IE" -> R.string.airwallex_billing_label_eircode
        else -> R.string.airwallex_billing_label_postal_code
    }
}
