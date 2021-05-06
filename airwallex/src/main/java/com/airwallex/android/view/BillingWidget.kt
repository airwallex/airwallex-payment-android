package com.airwallex.android.view

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.airwallex.android.R
import com.airwallex.android.databinding.WidgetBillingBinding
import com.airwallex.android.model.Address
import com.airwallex.android.model.Billing
import com.airwallex.android.model.Shipping

/**
 * A widget used to collect the [Billing] info
 */
internal class BillingWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val viewBinding = WidgetBillingBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val sameAsShippingViewGroup = viewBinding.rlSameAsShipping
    private val sameAsShippingSwitch = viewBinding.swSameAsShipping
    private val firstNameTextInputLayout = viewBinding.atlFirstName
    private val lastNameTextInputLayout = viewBinding.atlLastName
    private val countryAutocomplete = viewBinding.countryAutocomplete
    private val stateTextInputLayout = viewBinding.atlState
    private val cityTextInputLayout = viewBinding.atlCity
    private val addressTextInputLayout = viewBinding.atlStreetAddress
    private val zipcodeTextInputLayout = viewBinding.atlZipCode
    private val emailTextInputLayout = viewBinding.atlEmail
    private val numberTextInputLayout = viewBinding.atlPhoneNumber
    private val billingViewGroup = viewBinding.llBilling

    internal var billingChangeCallback: () -> Unit = {}

    private val keyboardController: KeyboardController by lazy {
        KeyboardController(context as Activity)
    }

    /**
     * Update UI via [Shipping]
     */
    internal var shipping: Shipping? = null
        set(value) {
            field = value
            if (value != null) {
                sameAsShippingViewGroup.visibility = View.VISIBLE
                sameAsShippingSwitch.isChecked = true
            } else {
                sameAsShippingViewGroup.visibility = View.GONE
                sameAsShippingSwitch.isChecked = false
            }
        }

    /**
     * Whether to use the same [Address] as in [Shipping]
     */
    private val sameAsShipping: Boolean
        get() {
            return sameAsShippingSwitch.isChecked
        }

    /**
     * Return [Billing] based on user input if valid, otherwise null.
     */
    internal val billing: Billing?
        get() {
            val shipping = this.shipping
            if (sameAsShipping && shipping != null) {
                return Billing.Builder()
                    .setFirstName(shipping.firstName)
                    .setLastName(shipping.lastName)
                    .setPhone(shipping.phoneNumber)
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
                val addressBuilder = Address.Builder()
                    .setCountryCode(countryAutocomplete.country)
                    .setState(stateTextInputLayout.value)
                    .setCity(cityTextInputLayout.value)
                    .setStreet(addressTextInputLayout.value)
                if (!TextUtils.isEmpty(zipcodeTextInputLayout.value)) {
                    addressBuilder.setPostcode(zipcodeTextInputLayout.value)
                }
                val address = addressBuilder.build()
                val billingBuilder = Billing.Builder()
                    .setFirstName(firstNameTextInputLayout.value)
                    .setLastName(lastNameTextInputLayout.value)
                    .setAddress(address)
                if (!TextUtils.isEmpty(emailTextInputLayout.value)) {
                    billingBuilder.setEmail(emailTextInputLayout.value)
                }
                if (!TextUtils.isEmpty(numberTextInputLayout.value)) {
                    billingBuilder.setPhone(numberTextInputLayout.value)
                }
                return billingBuilder.build()
            } else {
                return null
            }
        }

    /**
     * Check if billing is valid
     */
    internal val isValid: Boolean
        get() {
            return sameAsShipping ||
                !sameAsShipping &&
                firstNameTextInputLayout.value.isNotEmpty() &&
                lastNameTextInputLayout.value.isNotEmpty() &&
                countryAutocomplete.country != null &&
                stateTextInputLayout.value.isNotEmpty() &&
                cityTextInputLayout.value.isNotEmpty() &&
                addressTextInputLayout.value.isNotEmpty() &&
                (emailTextInputLayout.value.isEmpty() || emailTextInputLayout.value.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailTextInputLayout.value).matches())
        }

    init {
        countryAutocomplete.countryChangeCallback = {
            billingChangeCallback.invoke()
            stateTextInputLayout.requestInputFocus()
        }

        sameAsShippingSwitch.setOnCheckedChangeListener { _, isChecked ->
            billingViewGroup.visibility = if (isChecked) View.GONE else View.VISIBLE
            keyboardController.hide()
            billingChangeCallback.invoke()
        }

        listenTextChanged()
        listenFocusChanged()
    }

    private fun listenTextChanged() {
        firstNameTextInputLayout.afterTextChanged { billingChangeCallback.invoke() }
        lastNameTextInputLayout.afterTextChanged { billingChangeCallback.invoke() }
        emailTextInputLayout.afterTextChanged { billingChangeCallback.invoke() }
        stateTextInputLayout.afterTextChanged { billingChangeCallback.invoke() }
        cityTextInputLayout.afterTextChanged { billingChangeCallback.invoke() }
        addressTextInputLayout.afterTextChanged { billingChangeCallback.invoke() }
        zipcodeTextInputLayout.afterTextChanged { billingChangeCallback.invoke() }
    }

    private fun listenFocusChanged() {
        firstNameTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (firstNameTextInputLayout.value.isEmpty()) {
                    firstNameTextInputLayout.error = resources.getString(R.string.empty_first_name)
                } else {
                    firstNameTextInputLayout.error = null
                }
            } else {
                firstNameTextInputLayout.error = null
            }
        }

        lastNameTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (lastNameTextInputLayout.value.isEmpty()) {
                    lastNameTextInputLayout.error = resources.getString(R.string.empty_last_name)
                } else {
                    lastNameTextInputLayout.error = null
                }
            } else {
                lastNameTextInputLayout.error = null
            }
        }

        emailTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (emailTextInputLayout.value.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailTextInputLayout.value).matches()) {
                    emailTextInputLayout.error = resources.getString(R.string.invalid_email)
                } else {
                    emailTextInputLayout.error = null
                }
            } else {
                emailTextInputLayout.error = null
            }
        }

        stateTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (stateTextInputLayout.value.isEmpty()) {
                    stateTextInputLayout.error = resources.getString(R.string.empty_state)
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
                    cityTextInputLayout.error = resources.getString(R.string.empty_city)
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
                    addressTextInputLayout.error = resources.getString(R.string.empty_street)
                } else {
                    addressTextInputLayout.error = null
                }
            } else {
                addressTextInputLayout.error = null
            }
        }
    }
}
