package com.airwallex.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import com.airwallex.android.R
import com.airwallex.android.core.model.DynamicSchemaFieldCandidate
import com.airwallex.android.databinding.DynamicFieldAutoCompleteViewBinding
import java.util.*

@SuppressLint("ViewConstructor")
class DynamicFieldCompleteView constructor(
    context: Context,
    attrs: AttributeSet?,
    private val candidates: List<DynamicSchemaFieldCandidate>
) : FrameLayout(context, attrs) {

    private val viewBinding = DynamicFieldAutoCompleteViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var error: String? = null

    private val adapter: DynamicFieldAdapter by lazy {
        DynamicFieldAdapter(getContext(), candidates)
    }

    val value: String?
        get() {
            val fieldValue = viewBinding.dynamicField.text.toString()
            return if (candidates.find { it.value == fieldValue } != null) {
                fieldValue
            } else {
                null
            }
        }

    init {
        viewBinding.dynamicField.threshold = 0
        viewBinding.dynamicField.setAdapter(adapter)

        viewBinding.dynamicField.onFocusChangeListener = OnFocusChangeListener { _, focused ->
            error = if (focused) {
                viewBinding.dynamicField.showDropDown()
                null
            } else {
                val field = viewBinding.dynamicField.text.toString()
                viewBinding.dynamicField.setText(
                    if (candidates.find { it.value == field } != null) {
                        field
                    } else {
                        null
                    }
                )
                if (field.isBlank()) {
                    resources.getString(R.string.airwallex_empty_country)
                } else {
                    null
                }
            }
        }
    }

    fun setHint(hint: CharSequence) {
        viewBinding.tlField.hint = hint
    }

    fun setImeOptions(imeOptions: Int) {
        viewBinding.dynamicField.imeOptions = imeOptions
    }

    internal class DynamicFieldAdapter(
        context: Context,
        var candidates: List<DynamicSchemaFieldCandidate>
    ) : ArrayAdapter<DynamicSchemaFieldCandidate>(context, R.layout.dynamic_field_textview) {

        private val candidateFilter: CandidateFilter = CandidateFilter(
            candidates,
            this
        )

        override fun getCount(): Int {
            return candidates.size
        }

        override fun getItem(i: Int): DynamicSchemaFieldCandidate {
            return candidates[i]
        }

        override fun getItemId(i: Int): Long {
            return getItem(i).hashCode().toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            return if (view is TextView) {
                view.text = getItem(i).value
                view
            } else {
                val label = LayoutInflater.from(context).inflate(
                    R.layout.dynamic_field_textview, viewGroup, false
                ) as TextView
                label.text = getItem(i).value
                label
            }
        }

        override fun getFilter(): Filter {
            return candidateFilter
        }
    }

    private class CandidateFilter(
        var candidates: List<DynamicSchemaFieldCandidate>,
        private val adapter: DynamicFieldAdapter
    ) : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            filterResults.values = constraint?.let {
                filteredSuggestedCountries(constraint)
            } ?: candidates
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(
            constraint: CharSequence?,
            filterResults: FilterResults?
        ) {
            adapter.candidates =
                filterResults?.values as? List<DynamicSchemaFieldCandidate> ?: arrayListOf()
            adapter.notifyDataSetChanged()
        }

        private fun filteredSuggestedCountries(constraint: CharSequence?): List<DynamicSchemaFieldCandidate> {
            return candidates.filter {
                it.value.lowercase(Locale.ROOT).startsWith(
                    constraint.toString().lowercase(Locale.ROOT)
                )
            }
        }
    }
}
