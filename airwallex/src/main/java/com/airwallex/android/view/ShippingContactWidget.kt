package com.airwallex.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.airwallex.android.R
import com.airwallex.android.model.Shipping
import kotlinx.android.synthetic.main.widget_contact.view.*

/**
 * A widget used to collect the contact info of shipping info.
 */
internal class ShippingContactWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    /**
     * The listener of when the shipping contact changed
     */
    internal var contactChangeCallback: () -> Unit = {}

    /**
     * Validation rules for contact info
     */
    internal val isValidContact: Boolean
        get() {
            return atlLastName.value.isNotEmpty() &&
                atlFirstName.value.isNotEmpty()
        }

    /**
     * Return shipping contact info based on user input.
     */
    internal val shippingContact: Triple<String, String, String>
        get() {
            return Triple(atlLastName.value, atlFirstName.value, atlPhoneNumber.value)
        }

    init {
        View.inflate(
            getContext(),
            R.layout.widget_contact, this
        )

        listenTextChanged()
        listenFocusChanged()
    }

    internal fun initializeView(shipping: Shipping) {
        with(shipping) {
            atlLastName.value = lastName ?: ""
            atlFirstName.value = firstName ?: ""
            atlPhoneNumber.value = phoneNumber ?: ""
        }
    }

    private fun listenTextChanged() {
        atlLastName.afterTextChanged { contactChangeCallback.invoke() }
        atlFirstName.afterTextChanged { contactChangeCallback.invoke() }
    }

    private fun listenFocusChanged() {
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
    }
}
