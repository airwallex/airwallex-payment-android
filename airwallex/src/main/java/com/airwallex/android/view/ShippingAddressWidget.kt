package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.VisibleForTesting
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.R
import com.airwallex.android.databinding.WidgetShippingBinding

/**
 * A widget used to collect the shipping [Address] of shipping info.
 */
class ShippingAddressWidget(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {

    private val viewBinding = WidgetShippingBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    @VisibleForTesting
    val firstNameTextInputLayout = viewBinding.atlFirstName

    @VisibleForTesting
    val lastNameTextInputLayout = viewBinding.atlLastName

    @VisibleForTesting
    val stateTextInputLayout = viewBinding.atlState

    @VisibleForTesting
    val cityTextInputLayout = viewBinding.atlCity

    @VisibleForTesting
    val addressTextInputLayout = viewBinding.atlStreetAddress

    @VisibleForTesting
    val emailTextInputLayout = viewBinding.atlEmail

    private val zipcodeTextInputLayout = viewBinding.atlZipCode
    private val numberTextInputLayout = viewBinding.atlPhoneNumber
    private val countryAutocomplete = viewBinding.countryAutocomplete

    /**
     * The listener of when the shipping address changed
     */
    var shippingChangeCallback: () -> Unit = {}

    private var country: CountryAutoCompleteView.Country? = null

    /**
     * Return [Address] based on user input.
     */
    val shipping: Shipping
        get() {
            return Shipping.Builder()
                .setLastName(lastNameTextInputLayout.value)
                .setFirstName(firstNameTextInputLayout.value)
                .setPhone(numberTextInputLayout.value)
                .setEmail(emailTextInputLayout.value)
                .setAddress(
                    Address.Builder()
                        .setCountryCode(country?.code)
                        .setState(stateTextInputLayout.value)
                        .setCity(cityTextInputLayout.value)
                        .setStreet(addressTextInputLayout.value)
                        .setPostcode(zipcodeTextInputLayout.value)
                        .build()
                )
                .build()
        }

    /**
     * Validation rules for shipping address
     */
    val isValid: Boolean
        get() {
            return lastNameTextInputLayout.value.isNotEmpty() &&
                firstNameTextInputLayout.value.isNotEmpty() &&
                country != null &&
                stateTextInputLayout.value.isNotEmpty() &&
                cityTextInputLayout.value.isNotEmpty() &&
                addressTextInputLayout.value.isNotEmpty() &&
                (
                    emailTextInputLayout.value.isEmpty() ||
                        emailTextInputLayout.value.isNotEmpty() &&
                        Patterns.EMAIL_ADDRESS.matcher(emailTextInputLayout.value).matches()
                    )
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

    fun initializeView(shipping: Shipping) {
        with(shipping) {
            lastNameTextInputLayout.value = lastName ?: ""
            firstNameTextInputLayout.value = firstName ?: ""
            numberTextInputLayout.value = phoneNumber ?: ""
            emailTextInputLayout.value = email ?: ""
            addressTextInputLayout.value = address?.street ?: ""
            zipcodeTextInputLayout.value = address?.postcode ?: ""
            cityTextInputLayout.value = address?.city ?: ""
            stateTextInputLayout.value = address?.state ?: ""
            countryAutocomplete.setInitCountry(address?.countryCode)
        }
    }

    private fun listenTextChanged() {
        lastNameTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
        firstNameTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
        stateTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
        cityTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
        addressTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
        zipcodeTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
        emailTextInputLayout.afterTextChanged { shippingChangeCallback.invoke() }
    }

    private fun listenFocusChanged() {
        lastNameTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus && lastNameTextInputLayout.value.isEmpty()) {
                lastNameTextInputLayout.error =
                    resources.getString(R.string.airwallex_empty_last_name)
            } else {
                lastNameTextInputLayout.error = null
            }
        }

        firstNameTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus && firstNameTextInputLayout.value.isEmpty()) {
                firstNameTextInputLayout.error =
                    resources.getString(R.string.airwallex_empty_first_name)
            } else {
                firstNameTextInputLayout.error = null
            }
        }
        stateTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus && stateTextInputLayout.value.isEmpty()) {
                stateTextInputLayout.error = resources.getString(R.string.airwallex_empty_state)
            } else {
                stateTextInputLayout.error = null
            }
        }

        cityTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus && cityTextInputLayout.value.isEmpty()) {
                cityTextInputLayout.error = resources.getString(R.string.airwallex_empty_city)
            } else {
                cityTextInputLayout.error = null
            }
        }

        addressTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus && addressTextInputLayout.value.isEmpty()) {
                addressTextInputLayout.error =
                    resources.getString(R.string.airwallex_empty_street)
            } else {
                addressTextInputLayout.error = null
            }
        }

        emailTextInputLayout.afterFocusChanged { hasFocus ->
            if (!hasFocus && emailTextInputLayout.value.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(
                    emailTextInputLayout.value
                ).matches()
            ) {
                emailTextInputLayout.error =
                    resources.getString(R.string.airwallex_invalid_email)
            } else {
                emailTextInputLayout.error = null
            }
        }
    }
}
