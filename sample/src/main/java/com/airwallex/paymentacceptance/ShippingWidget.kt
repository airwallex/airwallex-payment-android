package com.airwallex.paymentacceptance

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.airwallex.android.model.Address
import com.airwallex.paymentacceptance.view.CountryAutoCompleteView
import kotlinx.android.synthetic.main.widget_shipping.view.*

class ShippingWidget(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs),
    TextWatcher {

    var shippingChangeCallback: (() -> Unit)? = null

    private var country: CountryAutoCompleteView.Country? = null

    fun getEditShipping(): Address {
        return Address.Builder()
            .setCountryCode(country?.code)
            .setState(etState.text.toString())
            .setCity(etCity.text.toString())
            .setStreet(etStreetAddress.text.toString())
            .setPostcode(etZipCode.text.toString())
            .build()
    }

    fun isValidShipping(): Boolean {
        return country != null
                && etState.text.isNotEmpty()
                && etCity.text.isNotEmpty()
                && etStreetAddress.text.isNotEmpty()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_shipping, this)

        countryAutocomplete.countryChangeCallback = { country ->
            this.country = country
        }

        PaymentData.shipping?.apply {
            etStreetAddress.setText(address?.street)
            etZipCode.setText(address?.postcode)
            etCity.setText(address?.city)
            etState.setText(address?.state)
            countryAutocomplete.setInitCountry(address?.countryCode)
        }

        etState.addTextChangedListener(this)
        etCity.addTextChangedListener(this)
        etStreetAddress.addTextChangedListener(this)
        etZipCode.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
        shippingChangeCallback?.invoke()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}