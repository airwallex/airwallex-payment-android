package com.airwallex.paymentacceptance

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.airwallex.android.model.Address
import com.airwallex.paymentacceptance.view.CountryAutoCompleteView
import kotlinx.android.synthetic.main.widget_contact.view.*
import kotlinx.android.synthetic.main.widget_shipping.view.*

class ShippingWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    var shippingChangeCallback: (() -> Unit)? = null

    private var country: CountryAutoCompleteView.Country? = null

    val shipping: Address
        get() {
            return Address.Builder()
                .setCountryCode(country?.code)
                .setState(atlState.text)
                .setCity(atlCity.text)
                .setStreet(atlStreetAddress.text)
                .setPostcode(atlZipCode.text)
                .build()
        }

    val isValidShipping: Boolean
        get() {
            return country != null
                    && atlState.text.isNotEmpty()
                    && atlCity.text.isNotEmpty()
                    && atlStreetAddress.text.isNotEmpty()
        }

    init {
        View.inflate(getContext(), R.layout.widget_shipping, this)

        countryAutocomplete.countryChangeCallback = { country ->
            this.country = country
            shippingChangeCallback?.invoke()
        }

        PaymentData.shipping?.apply {
            atlStreetAddress.text = address?.street ?: ""
            atlZipCode.text = address?.postcode ?: ""
            atlCity.text = address?.city ?: ""
            atlState.text = address?.state ?: ""
            countryAutocomplete.setInitCountry(address?.countryCode)
        }

        atlState.afterTextChanged {
            shippingChangeCallback?.invoke()
        }

        atlCity.afterTextChanged {
            shippingChangeCallback?.invoke()
        }

        atlStreetAddress.afterTextChanged {
            shippingChangeCallback?.invoke()
        }

        atlZipCode.afterTextChanged {
            shippingChangeCallback?.invoke()
        }

        atlState.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlState.text.isEmpty()) {
                    atlState.error = "Please enter your state"
                } else {
                    atlState.error = null
                }
            } else {
                atlState.error = null
            }
        }

        atlCity.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlCity.text.isEmpty()) {
                    atlCity.error = "Please enter your city"
                } else {
                    atlCity.error = null
                }
            } else {
                atlCity.error = null
            }
        }

        atlStreetAddress.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlStreetAddress.text.isEmpty()) {
                    atlStreetAddress.error = "Please enter your street"
                } else {
                    atlStreetAddress.error = null
                }
            } else {
                atlStreetAddress.error = null
            }
        }
    }
}