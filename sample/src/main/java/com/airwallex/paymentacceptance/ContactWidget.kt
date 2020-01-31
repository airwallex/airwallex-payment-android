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
            return etLastName.text.isNotEmpty()
                    && etFirstName.text.isNotEmpty()
                    && etEmail.text.isNotEmpty()
        }

    internal val contact: Contact
        get() {
            return Contact(
                lastName = etLastName.text.toString(),
                firstName = etFirstName.text.toString(),
                phone = etPhoneNumber.text.toString(),
                email = etEmail.text.toString()
            )
        }

    init {
        View.inflate(getContext(), R.layout.widget_contact, this)

        PaymentData.shipping?.apply {
            etLastName.setText(lastName)
            etFirstName.setText(firstName)
            etPhoneNumber.setText(phone)
            etEmail.setText(email)
        }

        etLastName.addTextChangedListener(this)
        etFirstName.addTextChangedListener(this)
        etPhoneNumber.addTextChangedListener(this)
        etEmail.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
        contactChangeCallback?.invoke()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}