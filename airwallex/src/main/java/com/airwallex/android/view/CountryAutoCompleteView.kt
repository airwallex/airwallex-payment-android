package com.airwallex.android.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toDrawable
import com.airwallex.android.R
import com.airwallex.android.databinding.CountryAutocompleteViewBinding
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.util.EditTextColorUtil
import com.airwallex.android.view.util.CountryUtils
import java.util.Locale

class CountryAutoCompleteView constructor(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs) {

    private val viewBinding = CountryAutocompleteViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var selectedCountry: Country? = null

    private var error: String? = null
        set(value) {
            field = value
            viewBinding.actCountry.error = value
        }

    private val countryAdapter: CountryAdapter by lazy {
        CountryAdapter(getContext(), CountryUtils.countryList)
    }

    /**
     * Country selected changed callback
     */
    var countryChangeCallback: (Country) -> Unit = {}

    var country: String? = null
        set(value) {
            value?.let {
                viewBinding.actCountry.setText(CountryUtils.getCountryByCode(it)?.name)
            }
            field = value
        }
        get() {
            return CountryUtils.getCountryByName(viewBinding.actCountry.text.toString())?.code
        }

    init {
        viewBinding.actCountry.setTextColor(AirwallexColor.textPrimary.toArgb())

        EditTextColorUtil.applyCursorColor(viewBinding.actCountry, AirwallexColor.theme, context)

        val boxStrokeStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_focused),
                intArrayOf()
            ),
            intArrayOf(
                AirwallexColor.theme.toArgb(),
                AirwallexColor.borderDecorativeStrong.toArgb()
            )
        )
        viewBinding.tlCountry.setBoxStrokeColorStateList(boxStrokeStateList)

        val hintStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_focused),
                intArrayOf()
            ),
            intArrayOf(
                AirwallexColor.theme.toArgb(),
                AirwallexColor.textPlaceholder.toArgb()
            )
        )
        viewBinding.tlCountry.hintTextColor = hintStateList
        viewBinding.tlCountry.defaultHintTextColor = hintStateList

        viewBinding.actCountry.threshold = 0
        viewBinding.actCountry.setAdapter(countryAdapter)
        viewBinding.actCountry.setDropDownBackgroundDrawable(
            AirwallexColor.backgroundSecondary.toArgb().toDrawable()
        )

        viewBinding.actCountry.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                updatedSelectedCountryCode(countryAdapter.getItem(position))
            }
        viewBinding.actCountry.onFocusChangeListener = OnFocusChangeListener { _, focused ->
            error = if (focused) {
                viewBinding.actCountry.showDropDown()
                null
            } else {
                val enteredCountry = viewBinding.actCountry.text.toString()
                val country = CountryUtils.getCountryByName(enteredCountry)

                val displayCountry = country?.let {
                    updatedSelectedCountryCode(it)
                    enteredCountry
                } ?: selectedCountry?.name

                viewBinding.actCountry.setText(displayCountry)

                if (displayCountry.isNullOrBlank()) {
                    resources.getString(R.string.airwallex_empty_country)
                } else {
                    null
                }
            }
        }
    }

    internal fun setInitCountry(countryCode: String?) {
        countryCode?.let {
            selectedCountry = CountryUtils.getCountryByCode(it)
            selectedCountry?.let { country ->
                viewBinding.actCountry.setText(country.name)
                countryChangeCallback.invoke(country)
            }
        }
    }

    fun updatedSelectedCountryCode(country: Country) {
        if (selectedCountry != country) {
            selectedCountry = country
            countryChangeCallback.invoke(country)
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
    ) : ArrayAdapter<Country>(context, R.layout.country_textview) {

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
                view.setTextColor(AirwallexColor.textPrimary.toArgb())
                view
            } else {
                val countryText = LayoutInflater.from(context).inflate(
                    R.layout.country_textview, viewGroup, false
                ) as TextView
                countryText.text = getItem(i).name
                countryText.setTextColor(AirwallexColor.textPrimary.toArgb())
                countryText
            }
        }

        override fun getFilter(): Filter {
            return countryFilter
        }
    }

    private class CountryFilter(
        var countries: List<Country>,
        private val adapter: CountryAdapter
    ) : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            filterResults.values = constraint?.let {
                filteredSuggestedCountries(constraint)
            } ?: countries
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(
            constraint: CharSequence?,
            filterResults: FilterResults?
        ) {
            adapter.countries = filterResults?.values as? List<Country> ?: arrayListOf()
            adapter.notifyDataSetChanged()
        }

        private fun filteredSuggestedCountries(constraint: CharSequence?): List<Country> {
            return countries.filter {
                it.name.lowercase(Locale.ROOT).startsWith(
                    constraint.toString().lowercase(Locale.ROOT)
                )
            }
        }
    }
}
