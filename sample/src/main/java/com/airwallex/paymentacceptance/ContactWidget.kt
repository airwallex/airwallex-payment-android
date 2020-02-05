package com.airwallex.paymentacceptance

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.widget_contact.view.*

class ContactWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs),
    TextWatcher {

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
                    && atlEmail.text.isNotEmpty()
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

        atlLastName.afterTextChanged {
            contactChangeCallback?.invoke()
        }

        atlFirstName.afterTextChanged {
            contactChangeCallback?.invoke()
        }

        atlEmail.afterTextChanged {
            contactChangeCallback?.invoke()
        }

        atlLastName.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlLastName.text.isEmpty()) {
                    atlLastName.error = "Please enter your last name"
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
                    atlFirstName.error = "Please enter your first name"
                } else {
                    atlFirstName.error = null
                }
            } else {
                atlFirstName.error = null
            }
        }

        atlEmail.afterFocusChanged { hasFocus ->
            if (!hasFocus) {
                if (atlEmail.text.isEmpty()) {
                    atlEmail.error = "Please enter your email"
                } else {
                    atlEmail.error = null
                }
            } else {
                atlEmail.error = null
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        contactChangeCallback?.invoke()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}