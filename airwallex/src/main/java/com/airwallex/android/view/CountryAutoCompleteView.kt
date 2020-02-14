package com.airwallex.android.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.airwallex.android.R
import kotlinx.android.synthetic.main.view_country_autocomplete.view.*
import java.util.*

class CountryAutoCompleteView constructor(
    context: Context,
    attrs: AttributeSet
) : FrameLayout(context, attrs) {

    private var selectedCountry: Country? = null

    var countryChangeCallback: ((Country) -> Unit)? = null

    private var error: String?
        set(value) {
            tvError.visibility = when (value) {
                null -> View.GONE
                else -> View.VISIBLE
            }

            tvError.text = value
            updateLayoutColor()
        }
        get() {
            return tvError.text.toString()
        }

    var country: String? = null
        set(value) {
            value?.let {
                actCountry.setText(getCountryByCode(it)?.name)
            }
            field = value
        }
        get() {
            return getCountryByName(actCountry.text.toString())?.code
        }

    private val legalCountries = arrayOf(
        "AC", "AD", "AE", "AG", "AI", "AL", "AM", "AN", "AO", "AQ", "AR", "AS", "AT", "AU", "AW",
        "AX", "AZ", "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BJ", "BL", "BM", "BN", "BO", "BQ",
        "BR", "BS", "BT", "BU", "BV", "BW", "BZ", "CA", "CC", "CG", "CH", "CI", "CK", "CL", "CM",
        "CN", "CO", "CP", "CR", "CS", "CV", "CW", "CX", "CY", "CZ", "DE", "DG", "DJ", "DK", "DM",
        "DO", "DZ", "EA", "EC", "EE", "EG", "EH", "ES", "ET", "EU", "EZ", "FI", "FJ", "FK", "FM",
        "FO", "FR", "FX", "GA", "GB", "GD", "GE", "GF", "GG", "GH", "GI", "GL", "GM", "GN", "GP",
        "GQ", "GR", "GS", "GT", "GU", "GY", "HK", "HM", "HN", "HR", "HT", "HU", "IC", "ID", "IE",
        "IL", "IM", "IN", "IO", "IS", "IT", "JE", "JM", "JO", "JP", "KE", "KG", "KH", "KI", "KM",
        "KN", "KR", "KW", "KY", "KZ", "LA", "LB", "LC", "LI", "LK", "LS", "LT", "LU", "LV", "MA",
        "MC", "MD", "ME", "MF", "MG", "MH", "MK", "ML", "MN", "MO", "MP", "MQ", "MR", "MS", "MT",
        "MU", "MV", "MW", "MX", "MY", "MZ", "NA", "NC", "NE", "NF", "NG", "NI", "NL", "NO", "NP",
        "NR", "NT", "NU", "NZ", "OM", "PA", "PE", "PF", "PG", "PH", "PK", "PL", "PM", "PN", "PR",
        "PS", "PT", "PW", "PY", "QA", "RE", "RO", "RS", "RU", "SA", "SB", "SC", "SE", "SF", "SG",
        "SH", "SI", "SJ", "SK", "SM", "SN", "SR", "ST", "SU", "SV", "SX", "SZ", "TA", "TC", "TD",
        "TF", "TG", "TH", "TJ", "TK", "TL", "TM", "TN", "TO", "TP", "TR", "TT", "TV", "TW", "TZ",
        "UA", "UG", "UK", "UM", "US", "UY", "UZ", "VA", "VC", "VE", "VG", "VI", "VN", "VU", "WF",
        "WS", "XK", "YT", "YU", "ZA", "ZM", "ZR"
    )

    private val countries: List<Country> =
        Locale.getISOCountries()
            .filter { code -> legalCountries.indexOf(code) >= 0 }
            .map { code ->
                Country(code, Locale("", code).displayCountry)
            }
            .sortedBy { it.name.toLowerCase(Locale.ROOT) }

    private val countryAdapter: CountryAdapter = CountryAdapter(getContext(), countries)

    private fun getCountryByName(countryName: String): Country? {
        return countries.firstOrNull { it.name == countryName }
    }

    private fun getCountryByCode(countryCode: String): Country? {
        return countries.firstOrNull { it.code == countryCode }
    }

    private fun updateLayoutColor() {
        if (error.isNullOrEmpty()) {
            vBorder.background =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.airwallex_input_layout_border,
                    null
                )
//            teInput.setHintTextColor(ContextCompat.getColor(context, R.color.colorEditTextAccent))
        } else {
            vBorder.background =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.airwallex_input_layout_border_error,
                    null
                )
//            teInput.setHintTextColor(ContextCompat.getColor(context, R.color.colorEditTextError))
        }
    }

    init {
        View.inflate(getContext(), R.layout.view_country_autocomplete, this)

        actCountry.threshold = 0
        actCountry.setAdapter(countryAdapter)

        actCountry.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            updatedSelectedCountryCode(countryAdapter.getItem(position))

            val inputMethodManager =
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (inputMethodManager?.isAcceptingText == true) {
                inputMethodManager.hideSoftInputFromWindow(
                    (getContext() as Activity).currentFocus?.windowToken,
                    0
                )
            }
        }
        actCountry.onFocusChangeListener = OnFocusChangeListener { _, focused ->
            if (focused) {
                actCountry.showDropDown()
                error = null
            } else {
                val enteredCountry = actCountry.text.toString()
                val country = getCountryByName(enteredCountry)

                val displayCountry = country?.let {
                    updatedSelectedCountryCode(it)
                    enteredCountry
                } ?: selectedCountry?.name

                actCountry.setText(displayCountry)

                error = if (displayCountry.isNullOrBlank()) {
                    resources.getString(R.string.empty_country)
                } else {
                    null
                }
            }
        }
    }

    fun setInitCountry(countryCode: String?) {
        countryCode?.let {
            selectedCountry = getCountryByCode(it)
            selectedCountry?.let { country ->
                actCountry.setText(country.name)
                countryChangeCallback?.invoke(country)
            }
        }
    }

    private fun updatedSelectedCountryCode(country: Country) {
        if (selectedCountry != country) {
            selectedCountry = country
            countryChangeCallback?.invoke(country)
        }
    }

    data class Country(
        val code: String,
        val name: String
    ) {
        override fun toString(): String = name
    }

    internal class CountryAdapter(
        context: Context,
        var countries: List<Country>
    ) : ArrayAdapter<Country>(context, R.layout.view_country_item) {

        private val countryFilter: CountryFilter = CountryFilter(
            countries,
            this
        )

        override fun getCount(): Int {
            return countries.size
        }

        override fun getItem(i: Int): Country {
            return countries[i]
        }

        override fun getItemId(i: Int): Long {
            return getItem(i).hashCode().toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            return if (view is TextView) {
                view.text = getItem(i).name
                view
            } else {
                val countryText = LayoutInflater.from(context).inflate(
                    R.layout.view_country_item, viewGroup, false
                ) as TextView
                countryText.text = getItem(i).name
                countryText
            }
        }

        override fun getFilter(): Filter {
            return countryFilter
        }
    }

    private class CountryFilter(
        internal var countries: List<Country>,
        private val adapter: CountryAdapter
    ) : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            filterResults.values = constraint?.let {
                filteredSuggestedCountries(constraint)
            } ?: countries
            return filterResults
        }

        override fun publishResults(
            constraint: CharSequence?,
            filterResults: FilterResults?
        ) {
            adapter.countries = filterResults?.values as? List<Country> ?: arrayListOf()
            adapter.notifyDataSetChanged()
        }

        private fun filteredSuggestedCountries(constraint: CharSequence?): List<Country> {
            return countries.filter {
                it.name.toLowerCase(Locale.ROOT).startsWith(
                    constraint.toString().toLowerCase(Locale.ROOT)
                )
            }
        }
    }
}
