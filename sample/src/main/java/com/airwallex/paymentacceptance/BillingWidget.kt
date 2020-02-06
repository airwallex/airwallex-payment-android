package com.airwallex.paymentacceptance

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import com.airwallex.android.model.Address
import com.airwallex.android.model.PaymentMethod
import com.airwallex.paymentacceptance.view.CountryAutoCompleteView
import kotlinx.android.synthetic.main.widget_billing.view.*
import kotlinx.android.synthetic.main.widget_billing.view.atlEmail
import kotlinx.android.synthetic.main.widget_billing.view.atlFirstName
import kotlinx.android.synthetic.main.widget_billing.view.atlLastName
import kotlinx.android.synthetic.main.widget_billing.view.atlPhoneNumber

class BillingWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    var billingChangeCallback: (() -> Unit)? = null

    private var country: CountryAutoCompleteView.Country? = null

    val billing: PaymentMethod.Billing
        get() {
            return PaymentMethod.Billing.Builder()
                .setFirstName(atlFirstName.text)
                .setLastName(atlLastName.text)
                .setEmail(atlEmail.text)
                .setPhone(atlPhoneNumber.text)
                .setAddress(
                    Address.Builder()
                        .setCountryCode(country?.code)
                        .setState(atlState.text)
                        .setCity(atlCity.text)
                        .setStreet(atlStreetAddress.text)
                        .setPostcode(atlZipCode.text)
                        .build()
                )
                .build()
        }

    val isValidBilling: Boolean
        get() {
            return country != null
                    && atlState.text.isNotEmpty()
                    && atlCity.text.isNotEmpty()
                    && atlStreetAddress.text.isNotEmpty()
        }

    init {
        View.inflate(getContext(), R.layout.widget_billing, this)

        countryAutocomplete.countryChangeCallback = { country ->
            this.country = country
            billingChangeCallback?.invoke()
        }

        PaymentData.billing?.apply {
            atlFirstName.text = firstName ?: ""
            atlLastName.text = lastName ?: ""
            atlEmail.text = email ?: ""
            atlPhoneNumber.text = phone ?: ""
            atlStreetAddress.text = address?.street ?: ""
            atlZipCode.text = address?.postcode ?: ""
            atlCity.text = address?.city ?: ""
            atlState.text = address?.state ?: ""
            countryAutocomplete.setInitCountry(address?.countryCode)
        }

        listenTextChanged()
        listenFocusChanged()
    }

    private fun listenTextChanged() {
        atlFirstName.afterTextChanged { billingChangeCallback?.invoke() }
        atlLastName.afterTextChanged { billingChangeCallback?.invoke() }
        atlEmail.afterTextChanged { billingChangeCallback?.invoke() }
        atlState.afterTextChanged { billingChangeCallback?.invoke() }
        atlCity.afterTextChanged { billingChangeCallback?.invoke() }
        atlStreetAddress.afterTextChanged { billingChangeCallback?.invoke() }
        atlZipCode.afterTextChanged { billingChangeCallback?.invoke() }
    }

    private fun listenFocusChanged() {
        atlFirstName.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlFirstName.text.isEmpty()) {
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
                if (atlLastName.text.isEmpty()) {
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
                    atlEmail.text.isEmpty() -> {
                        atlEmail.error = resources.getString(R.string.empty_email)
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(atlEmail.text).matches() -> {
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
                if (atlState.text.isEmpty()) {
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
                if (atlCity.text.isEmpty()) {
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
                if (atlStreetAddress.text.isEmpty()) {
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