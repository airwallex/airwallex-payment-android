package com.airwallex.paymentacceptance

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.airwallex.android.model.Address
import com.neovisionaries.i18n.CountryCode
import kotlinx.android.synthetic.main.layout_edit_shipping.view.*

class EditShippingLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs),
    TextWatcher {

    private val legalCountries = arrayOf(
        "AC",
        "AD",
        "AE",
        "AG",
        "AI",
        "AL",
        "AM",
        "AN",
        "AO",
        "AQ",
        "AR",
        "AS",
        "AT",
        "AU",
        "AW",
        "AX",
        "AZ",
        "BA",
        "BB",
        "BD",
        "BE",
        "BF",
        "BG",
        "BH",
        "BJ",
        "BL",
        "BM",
        "BN",
        "BO",
        "BQ",
        "BR",
        "BS",
        "BT",
        "BU",
        "BV",
        "BW",
        "BZ",
        "CA",
        "CC",
        "CG",
        "CH",
        "CI",
        "CK",
        "CL",
        "CM",
        "CN",
        "CO",
        "CP",
        "CR",
        "CS",
        "CV",
        "CW",
        "CX",
        "CY",
        "CZ",
        "DE",
        "DG",
        "DJ",
        "DK",
        "DM",
        "DO",
        "DZ",
        "EA",
        "EC",
        "EE",
        "EG",
        "EH",
        "ES",
        "ET",
        "EU",
        "EZ",
        "FI",
        "FJ",
        "FK",
        "FM",
        "FO",
        "FR",
        "FX",
        "GA",
        "GB",
        "GD",
        "GE",
        "GF",
        "GG",
        "GH",
        "GI",
        "GL",
        "GM",
        "GN",
        "GP",
        "GQ",
        "GR",
        "GS",
        "GT",
        "GU",
        "GY",
        "HK",
        "HM",
        "HN",
        "HR",
        "HT",
        "HU",
        "IC",
        "ID",
        "IE",
        "IL",
        "IM",
        "IN",
        "IO",
        "IS",
        "IT",
        "JE",
        "JM",
        "JO",
        "JP",
        "KE",
        "KG",
        "KH",
        "KI",
        "KM",
        "KN",
        "KR",
        "KW",
        "KY",
        "KZ",
        "LA",
        "LB",
        "LC",
        "LI",
        "LK",
        "LS",
        "LT",
        "LU",
        "LV",
        "MA",
        "MC",
        "MD",
        "ME",
        "MF",
        "MG",
        "MH",
        "MK",
        "ML",
        "MN",
        "MO",
        "MP",
        "MQ",
        "MR",
        "MS",
        "MT",
        "MU",
        "MV",
        "MW",
        "MX",
        "MY",
        "MZ",
        "NA",
        "NC",
        "NE",
        "NF",
        "NG",
        "NI",
        "NL",
        "NO",
        "NP",
        "NR",
        "NT",
        "NU",
        "NZ",
        "OM",
        "PA",
        "PE",
        "PF",
        "PG",
        "PH",
        "PK",
        "PL",
        "PM",
        "PN",
        "PR",
        "PS",
        "PT",
        "PW",
        "PY",
        "QA",
        "RE",
        "RO",
        "RS",
        "RU",
        "SA",
        "SB",
        "SC",
        "SE",
        "SF",
        "SG",
        "SH",
        "SI",
        "SJ",
        "SK",
        "SM",
        "SN",
        "SR",
        "ST",
        "SU",
        "SV",
        "SX",
        "SZ",
        "TA",
        "TC",
        "TD",
        "TF",
        "TG",
        "TH",
        "TJ",
        "TK",
        "TL",
        "TM",
        "TN",
        "TO",
        "TP",
        "TR",
        "TT",
        "TV",
        "TW",
        "TZ",
        "UA",
        "UG",
        "UK",
        "UM",
        "US",
        "UY",
        "UZ",
        "VA",
        "VC",
        "VE",
        "VG",
        "VI",
        "VN",
        "VU",
        "WF",
        "WS",
        "XK",
        "YT",
        "YU",
        "ZA",
        "ZM",
        "ZR"
    )

    var onShippingChanged: (() -> Unit)? = null

    fun getEditShipping(): Address {
        return Address.Builder()
            .setCountryCode(
                CountryCode.values().find { it.getName() == etCountry.text.toString() }?.name
                    ?: ""
            )
            .setState(etState.text.toString())
            .setCity(etCity.text.toString())
            .setStreet(etStreetAddress.text.toString())
            .setPostcode(etZipCode.text.toString())
            .build()
    }

    fun isValidShipping(): Boolean {
        return etCountry.text.isNotEmpty()
                && etState.text.isNotEmpty()
                && etCity.text.isNotEmpty()
                && etStreetAddress.text.isNotEmpty()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_edit_shipping, this)

        PaymentData.shipping?.apply {
            etStreetAddress.setText(address?.street)
            etZipCode.setText(address?.postcode)
            etCity.setText(address?.city)
            etState.setText(address?.state)
            etCountry.setText(CountryCode.values().find { it.name == address?.countryCode }?.getName())
        }

        etCountry.addTextChangedListener(this)
        etState.addTextChangedListener(this)
        etCity.addTextChangedListener(this)
        etStreetAddress.addTextChangedListener(this)
        etZipCode.addTextChangedListener(this)

        etCountry.setOnClickListener {
            val codes = CountryCode.values()
                .filter { it.name != "UNDEFINED" && legalCountries.contains(it.name) }
                .map { it.getName() }
                .toTypedArray()
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setItems(codes) { dialog, which ->
                dialog.dismiss()
                etCountry.setText(codes[which])
            }
            builder.show()
        }
    }

    override fun afterTextChanged(s: Editable?) {
        onShippingChanged?.invoke()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }


}