package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.airwallex.android.R
import com.airwallex.android.model.Address
import com.airwallex.android.model.Shipping
import kotlinx.android.synthetic.main.widget_shipping.view.*

/**
 * A widget used to collect the shipping [Address] of shipping info.
 */
internal class ShippingAddressWidget(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {

    /**
     * The listener of when the shipping address changed
     */
    internal var shippingChangeCallback: () -> Unit = {}

    private var country: CountryAutoCompleteView.Country? = null

    /**
     * Return [Address] based on user input.
     */
    internal val address: Address?
        get() {
            return Address.Builder()
                .setCountryCode(country?.code)
                .setState(atlState.value)
                .setCity(atlCity.value)
                .setStreet(atlStreetAddress.value)
                .setPostcode(atlZipCode.value)
                .build()
        }

    /**
     * Validation rules for shipping address
     */
    internal val isValidShipping: Boolean
        get() {
            return country != null &&
                atlState.value.isNotEmpty() &&
                atlCity.value.isNotEmpty() &&
                atlStreetAddress.value.isNotEmpty()
        }

    init {
        View.inflate(
            getContext(),
            R.layout.widget_shipping, this
        )

        countryAutocomplete.countryChangeCallback = { country ->
            this.country = country
            shippingChangeCallback.invoke()
            atlState.requestInputFocus()
        }

        listenTextChanged()
        listenFocusChanged()
    }

    internal fun initializeView(shipping: Shipping) {
        with(shipping) {
            atlStreetAddress.value = address?.street ?: ""
            atlZipCode.value = address?.postcode ?: ""
            atlCity.value = address?.city ?: ""
            atlState.value = address?.state ?: ""
            countryAutocomplete.setInitCountry(address?.countryCode)
        }
    }

    private fun listenTextChanged() {
        atlState.afterTextChanged { shippingChangeCallback.invoke() }
        atlCity.afterTextChanged { shippingChangeCallback.invoke() }
        atlStreetAddress.afterTextChanged { shippingChangeCallback.invoke() }
        atlZipCode.afterTextChanged { shippingChangeCallback.invoke() }
    }

    private fun listenFocusChanged() {
        atlState.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlState.value.isEmpty()) {
                    atlState.error = resources.getString(R.string.empty_state)
                } else {
                    atlState.error = null
                }
            } else {
                atlState.error = null
            }
        }

        atlCity.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlCity.value.isEmpty()) {
                    atlCity.error = resources.getString(R.string.empty_city)
                } else {
                    atlCity.error = null
                }
            } else {
                atlCity.error = null
            }
        }

        atlStreetAddress.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlStreetAddress.value.isEmpty()) {
                    atlStreetAddress.error = resources.getString(R.string.empty_street)
                } else {
                    atlStreetAddress.error = null
                }
            } else {
                atlStreetAddress.error = null
            }
        }

        atlZipCode.afterFocusChanged {}
    }
}
