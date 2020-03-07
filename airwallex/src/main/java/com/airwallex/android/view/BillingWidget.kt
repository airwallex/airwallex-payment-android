package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import com.airwallex.android.R
import com.airwallex.android.model.Address
import com.airwallex.android.model.Billing
import com.airwallex.android.model.Shipping
import kotlinx.android.synthetic.main.widget_billing.view.*

internal class BillingWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    internal var billingChangeCallback: () -> Unit = {}

    var shipping: Shipping? = null
        set(value) {
            field = value
            if (value != null) {
                rlSameAsShipping.visibility = View.VISIBLE
                swSameAsShipping.isChecked = true
            } else {
                rlSameAsShipping.visibility = View.GONE
                swSameAsShipping.isChecked = false
            }
        }

    private val sameAsShipping: Boolean
        get() {
            return swSameAsShipping.isChecked
        }

    internal val billing: Billing?
        get() {
            val shipping = this.shipping
            if (sameAsShipping && shipping != null) {
                return Billing.Builder()
                    .setFirstName(shipping.firstName)
                    .setLastName(shipping.lastName)
                    .setEmail("jim631@sina.com") // TODO ERROR Server without email field
                    .setPhone(shipping.phone)
                    .setAddress(
                        shipping.address?.apply {
                            Address.Builder()
                                .setCountryCode(countryCode)
                                .setState(state)
                                .setCity(city)
                                .setStreet(street)
                                .setPostcode(postcode)
                                .build()
                        }
                    )
                    .build()
            } else if (isValid) {
                return Billing.Builder()
                    .setFirstName(atlFirstName.value)
                    .setLastName(atlLastName.value)
                    .setEmail(atlEmail.value)
                    .setPhone(atlPhoneNumber.value)
                    .setAddress(
                        Address.Builder()
                            .setCountryCode(countryAutocomplete.country)
                            .setState(atlState.value)
                            .setCity(atlCity.value)
                            .setStreet(atlStreetAddress.value)
                            .setPostcode(atlZipCode.value)
                            .build()
                    )
                    .build()
            } else {
                return null
            }
        }

    internal val isValid: Boolean
        get() {
            return sameAsShipping ||
                    !sameAsShipping &&
                    atlFirstName.value.isNotEmpty() &&
                    atlLastName.value.isNotEmpty() &&
                    countryAutocomplete.country != null &&
                    atlState.value.isNotEmpty() &&
                    atlCity.value.isNotEmpty() &&
                    atlStreetAddress.value.isNotEmpty() &&
                    atlEmail.value.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(atlEmail.value).matches()
        }

    init {
        View.inflate(getContext(), R.layout.widget_billing, this)

        countryAutocomplete.countryChangeCallback = {
            billingChangeCallback.invoke()
            atlState.requestInputFocus()
        }

        swSameAsShipping.setOnCheckedChangeListener { _, isChecked ->
            llBilling.visibility = if (isChecked) View.GONE else View.VISIBLE
            billingChangeCallback.invoke()
        }

        listenTextChanged()
        listenFocusChanged()
    }

    private fun listenTextChanged() {
        atlFirstName.afterTextChanged { billingChangeCallback.invoke() }
        atlLastName.afterTextChanged { billingChangeCallback.invoke() }
        atlEmail.afterTextChanged { billingChangeCallback.invoke() }
        atlState.afterTextChanged { billingChangeCallback.invoke() }
        atlCity.afterTextChanged { billingChangeCallback.invoke() }
        atlStreetAddress.afterTextChanged { billingChangeCallback.invoke() }
        atlZipCode.afterTextChanged { billingChangeCallback.invoke() }
    }

    private fun listenFocusChanged() {
        atlFirstName.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlFirstName.value.isEmpty()) {
                    atlFirstName.error = resources.getString(R.string.empty_first_name)
                } else {
                    atlFirstName.error = null
                }
            } else {
                atlFirstName.error = null
            }
        }

        atlLastName.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlLastName.value.isEmpty()) {
                    atlLastName.error = resources.getString(R.string.empty_last_name)
                } else {
                    atlLastName.error = null
                }
            } else {
                atlLastName.error = null
            }
        }

        atlEmail.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                when {
                    atlEmail.value.isEmpty() -> {
                        atlEmail.error = resources.getString(R.string.empty_email)
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(atlEmail.value).matches() -> {
                        atlEmail.error = resources.getString(R.string.invalid_email)
                    }
                    else -> {
                        atlEmail.error = null
                    }
                }
            } else {
                atlEmail.error = null
            }
        }

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
    }
}
