package com.airwallex.paymentacceptance.view

import com.google.android.material.textfield.TextInputLayout

internal class ErrorListener(
    private val textInputLayout: TextInputLayout
) : AirwallexEditText.ErrorMessageListener {

    override fun displayErrorMessage(message: String?) {
        if (message == null) {
            textInputLayout.isErrorEnabled = false
        } else {
            textInputLayout.error = message
        }
    }
}
