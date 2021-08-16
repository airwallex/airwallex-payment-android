package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.R
import com.airwallex.android.databinding.WidgetContactBinding

/**
 * A widget used to collect the contact info of shipping info.
 */
class ShippingContactWidget(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

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
    var contactChangeCallback: () -> Unit = {}

    /**
     * Validation rules for contact info
     */
    val isValidContact: Boolean
        get() {
            return lastNameTextInputLayout.value.isNotEmpty() &&
                firstNameTextInputLayout.value.isNotEmpty()
        }

    /**
     * Return shipping contact info based on user input.
     */
    val shippingContact: Triple<String, String, String>
        get() {
            return Triple(
                lastNameTextInputLayout.value,
                firstNameTextInputLayout.value,
                numberTextInputLayout.value
            )
        }

    init {
        listenTextChanged()
        listenFocusChanged()
    }

    fun initializeView(shipping: Shipping) {
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
                    lastNameTextInputLayout.error =
                        resources.getString(R.string.airwallex_empty_last_name)
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
                    firstNameTextInputLayout.error =
                        resources.getString(R.string.airwallex_empty_first_name)
                } else {
                    firstNameTextInputLayout.error = null
                }
            } else {
                firstNameTextInputLayout.error = null
            }
        }
    }
}
