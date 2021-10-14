package com.airwallex.android.view

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout.LayoutParams
import android.widget.Toast
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.databinding.DialogPaymentInfoBinding
import com.airwallex.android.R
import com.airwallex.android.core.model.*
import com.airwallex.android.core.model.PaymentMethodTypeInfoSchemaField.Companion.BANK_NAME

class PaymentInfoBottomSheetDialog : BottomSheetDialog() {

    var onCompleted: ((fieldMap: MutableMap<String, String>) -> Unit)? = null

    companion object {
        private const val PAYMENT_METHOD_TYPE_INFO = "payment_method_type_info"

        fun newInstance(
            paymentMethodTypeInfo: PaymentMethodTypeInfo
        ): PaymentInfoBottomSheetDialog {
            val args = Bundle()
            args.putParcelable(PAYMENT_METHOD_TYPE_INFO, paymentMethodTypeInfo)
            val fragment = PaymentInfoBottomSheetDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewBinding: DialogPaymentInfoBinding by lazy {
        DialogPaymentInfoBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val paymentMethodTypeInfo =
            arguments?.getParcelable<PaymentMethodTypeInfo>(PAYMENT_METHOD_TYPE_INFO)
        val fields = paymentMethodTypeInfo
            ?.fieldSchemas
            ?.firstOrNull()
            ?.fields
            ?.filter {
                it.name != BANK_NAME && !it.hidden
            }
            ?: return

        viewBinding.title.text = paymentMethodTypeInfo.displayName

        fields.forEach { field ->
            val input = AirwallexTextInputLayout(requireContext(), null)
            input.tag = field.name
            input.setHint(field.displayName)
            when (field.uiType) {
                PaymentMethodTypeInfoSchemaFieldUIType.TEXT -> input.setInputType(InputType.TYPE_CLASS_TEXT)
                PaymentMethodTypeInfoSchemaFieldUIType.EMAIL -> input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                PaymentMethodTypeInfoSchemaFieldUIType.PHONE -> input.setInputType(InputType.TYPE_CLASS_PHONE)
                else -> Unit
            }

            viewBinding.content.addView(
                input,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    topMargin = resources.getDimension(R.dimen.airwallex_marginTop_20).toInt()
                }
            )
        }

        for (i in 0 until viewBinding.content.childCount) {
            (viewBinding.content.getChildAt(i) as AirwallexTextInputLayout)
                .setImeOptions(
                    if (i == viewBinding.content.childCount - 1)
                        EditorInfo.IME_ACTION_DONE
                    else
                        EditorInfo.IME_ACTION_NEXT
                )
        }

        viewBinding.checkout.setOnSingleClickListener {
            val fieldMap = mutableMapOf<String, String>()
            for (i in 0 until viewBinding.content.childCount) {
                val input = (viewBinding.content.getChildAt(i) as AirwallexTextInputLayout)
                val field = fields.find { it.name == input.tag }
                val validations = field?.validations

                if (validations != null) {
                    if (isInvalid(input.value, validations)) {
                        Toast.makeText(
                            context,
                            getString(R.string.invalid_field, field.displayName),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnSingleClickListener
                    }
                }
                field?.let {
                    fieldMap[it.name] = input.value
                }
            }
            onCompleted?.invoke(fieldMap)
        }
    }

    private fun isInvalid(
        text: String,
        Validations: PaymentMethodTypeInfoSchemaFieldValidation
    ): Boolean {
        val regex = Validations.regex
        val max = Validations.max

        return regex != null && !Regex(regex).matches(text) ||
            max != null && text.length > max
    }
}
