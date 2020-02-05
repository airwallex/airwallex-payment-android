package com.airwallex.paymentacceptance

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.widget_contact.view.*

class ContactWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    internal data class Contact(
        val lastName: String,
        val firstName: String,
        val phone: String,
        val email: String
    )

    var contactChangeCallback: (() -> Unit)? = null

    val isValidContact: Boolean
        get() {
            return atlLastName.text.isNotEmpty()
                    && atlFirstName.text.isNotEmpty()
                    && atlEmail.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(atlEmail.text).matches()
        }

    internal val contact: Contact
        get() {
            return Contact(
                lastName = atlLastName.text,
                firstName = atlFirstName.text,
                phone = atlPhoneNumber.text,
                email = atlEmail.text
            )
        }

    init {
        View.inflate(getContext(), R.layout.widget_contact, this)

        PaymentData.shipping?.apply {
            atlLastName.text = lastName ?: ""
            atlFirstName.text = firstName ?: ""
            atlPhoneNumber.text = phone ?: ""
            atlEmail.text = email ?: ""
        }

        listenTextChanged()
        listenFocusChanged()
    }

    private fun listenTextChanged() {
        atlLastName.afterTextChanged { contactChangeCallback?.invoke() }
        atlFirstName.afterTextChanged { contactChangeCallback?.invoke() }
        atlEmail.afterTextChanged { contactChangeCallback?.invoke() }
    }

    private fun listenFocusChanged() {
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
    }
}