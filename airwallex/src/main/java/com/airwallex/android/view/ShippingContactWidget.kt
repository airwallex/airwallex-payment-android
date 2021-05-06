package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.airwallex.android.R
import com.airwallex.android.databinding.WidgetContactBinding
import com.airwallex.android.model.Shipping

/**
 * A widget used to collect the contact info of shipping info.
 */
internal class ShippingContactWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val viewBinding = WidgetContactBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val firstNameTextInputLayout = viewBinding.atlFirstName
    private val lastNameTextInputLayout = viewBinding.atlLastName
    private val numberTextInputLayout = viewBinding.atlPhoneNumber

    /**
     * The listener of when the shipping contact changed
     */
    internal var contactChangeCallback: () -> Unit = {}

    /**
     * Validation rules for contact info
     */
    internal val isValidContact: Boolean
        get() {
            return lastNameTextInputLayout.value.isNotEmpty() &&
                firstNameTextInputLayout.value.isNotEmpty()
        }

    /**
     * Return shipping contact info based on user input.
     */
    internal val shippingContact: Triple<String, String, String>
        get() {
            return Triple(lastNameTextInputLayout.value, firstNameTextInputLayout.value, numberTextInputLayout.value)
        }

    init {
        listenTextChanged()
        listenFocusChanged()
    }

    internal fun initializeView(shipping: Shipping) {
        with(shipping) {
            lastNameTextInputLayout.value = lastName ?: ""
            firstNameTextInputLayout.value = firstName ?: ""
            numberTextInputLayout.value = phoneNumber ?: ""
        }
    }

    private fun listenTextChanged() {
        lastNameTextInputLayout.afterTextChanged { contactChangeCallback.invoke() }
        firstNameTextInputLayout.afterTextChanged { contactChangeCallback.invoke() }
    }

    private fun listenFocusChanged() {
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
    }
}
