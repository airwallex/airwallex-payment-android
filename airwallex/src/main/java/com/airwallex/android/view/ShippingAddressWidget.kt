package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.airwallex.android.R
import com.airwallex.android.databinding.WidgetShippingBinding
import com.airwallex.android.model.Address
import com.airwallex.android.model.Shipping

/**
 * A widget used to collect the shipping [Address] of shipping info.
 */
internal class ShippingAddressWidget(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {

    private val viewBinding = WidgetShippingBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val countryAutocomplete = viewBinding.countryAutocomplete
    private val stateTextInputLayout = viewBinding.atlState
    private val cityTextInputLayout = viewBinding.atlCity
    private val addressTextInputLayout = viewBinding.atlStreetAddress
    private val zipcodeTextInputLayout = viewBinding.atlZipCode

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
                .setState(stateTextInputLayout.value)
                .setCity(cityTextInputLayout.value)
                .setStreet(addressTextInputLayout.value)
                .setPostcode(zipcodeTextInputLayout.value)
                .build()
        }

    /**
     * Validation rules for shipping address
     */
    internal val isValidShipping: Boolean
        get() {
            return country != null &&
                stateTextInputLayout.value.isNotEmpty() &&
                cityTextInputLayout.value.isNotEmpty() &&
                addressTextInputLayout.value.isNotEmpty()
        }

    init {
        countryAutocomplete.countryChangeCallback = { country ->
            this.country = country
            shippingChangeCallback.invoke()
            stateTextInputLayout.requestInputFocus()
        }

        listenTextChanged()
        listenFocusChanged()
    }

    internal fun initializeView(shipping: Shipping) {
        with(shipping) {
            addressTextInputLayout.value = address?.street ?: ""
            zipcodeTextInputLayout.value = address?.postcode ?: ""
            cityTextInputLayout.value = address?.city ?: ""
            stateTextInputLayout.value = address?.state ?: ""
            countryAutocomplete.setInitCountry(address?.countryCode)
        }
    }

    private fun listenTextChanged() {
        stateTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
        cityTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
        addressTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
        zipcodeTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
    }

    private fun listenFocusChanged() {
        stateTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (stateTextInputLayout.value.isEmpty()) {
                    stateTextInputLayout.error = resources.getString(R.string.airwallex_empty_state)
                } else {
                    stateTextInputLayout.error = null
                }
            } else {
                stateTextInputLayout.error = null
            }
        }

        cityTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (cityTextInputLayout.value.isEmpty()) {
                    cityTextInputLayout.error = resources.getString(R.string.airwallex_empty_city)
                } else {
                    cityTextInputLayout.error = null
                }
            } else {
                cityTextInputLayout.error = null
            }
        }

        addressTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (addressTextInputLayout.value.isEmpty()) {
                    addressTextInputLayout.error = resources.getString(R.string.airwallex_empty_street)
                } else {
                    addressTextInputLayout.error = null
                }
            } else {
                addressTextInputLayout.error = null
            }
        }
    }
}
