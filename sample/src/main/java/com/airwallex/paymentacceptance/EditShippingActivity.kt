package com.airwallex.paymentacceptance

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.model.Address
import com.airwallex.android.model.PaymentMethod
import com.neovisionaries.i18n.CountryCode
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_edit_shipping.*

class EditShippingActivity : AppCompatActivity(), TextWatcher {

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
    private val compositeSubscription = CompositeDisposable()
    private var menu: Menu? = null
    private val shipping = PaymentData.shipping

    companion object {

        const val SHIPPING_DETAIL = "SHIPPING_DETAIL"

        fun startActivityForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(
                Intent(activity, EditShippingActivity::class.java),
                requestCode
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_shipping)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        etZipCode.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                actionSave()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        shipping?.apply {
            etFirstName.setText(firstName)
            etLastName.setText(lastName)
            etStreetAddress.setText(address?.street)
            etZipCode.setText(address?.postcode)
            etCity.setText(address?.city)
            etState.setText(address?.state)
            etCountry.setText(CountryCode.values().find { it.name == address?.countryCode }?.getName())
            etPhoneNumber.setText(phone)
            etEmail.setText(email)
        }

        etLastName.addTextChangedListener(this)
        etFirstName.addTextChangedListener(this)
        etPhoneNumber.addTextChangedListener(this)
        etCountry.addTextChangedListener(this)
        etState.addTextChangedListener(this)
        etCity.addTextChangedListener(this)
        etStreetAddress.addTextChangedListener(this)
        etZipCode.addTextChangedListener(this)
        etEmail.addTextChangedListener(this)

        etCountry.setOnClickListener {
            val codes = CountryCode.values()
                .filter { it.name != "UNDEFINED" && legalCountries.contains(it.name) }
                .map { it.getName() }
                .toTypedArray()
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setItems(codes) { dialog, which ->
                dialog.dismiss()
                etCountry.setText(codes[which])
            }
            builder.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_save -> {
                actionSave()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(IllegalArgumentException::class)
    private fun actionSave() {
        val shipping = PaymentMethod.Billing.Builder()
            .setLastName(etLastName.text.toString())
            .setFirstName(etFirstName.text.toString())
            .setPhone(etPhoneNumber.text.toString())
            .setAddress(
                Address.Builder()
                    .setCountryCode(
                        CountryCode.values().find { it.getName() == etCountry.text.toString() }?.name
                            ?: ""
                    )
                    .setState(etState.text.toString())
                    .setCity(etCity.text.toString())
                    .setStreet(etStreetAddress.text.toString())
                    .setPostcode(etZipCode.text.toString())
                    .build()
            )
            .build()

        val intent = Intent()
        intent.putExtra(SHIPPING_DETAIL, shipping)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        compositeSubscription.dispose()
        super.onDestroy()
    }

    override fun afterTextChanged(s: Editable?) {
        menu?.findItem(R.id.menu_save)?.isEnabled = etLastName.text.isNotEmpty()
                && etFirstName.text.isNotEmpty()
                && etEmail.text.isNotEmpty()
                && etCountry.text.isNotEmpty()
                && etState.text.isNotEmpty()
                && etCity.text.isNotEmpty()
                && etStreetAddress.text.isNotEmpty()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}