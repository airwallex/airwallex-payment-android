package com.airwallex.android.view

import android.os.Bundle
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout.LayoutParams
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.databinding.DialogPaymentInfoBinding
import com.airwallex.android.R
import com.airwallex.android.core.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PaymentInfoBottomSheetDialog : BottomSheetDialog<DialogPaymentInfoBinding>() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val paymentMethodTypeInfo =
            arguments?.getParcelable<PaymentMethodTypeInfo>(PAYMENT_METHOD_TYPE_INFO)
        val fields = paymentMethodTypeInfo
            ?.fieldSchemas
            ?.firstOrNull { schema -> schema.transactionMode == TransactionMode.ONE_OFF }
            ?.fields
            ?.filter {
                it.type != DynamicSchemaFieldType.BANKS && !it.hidden
            }
            ?: return

        binding.title.text = paymentMethodTypeInfo.displayName

        fields.forEach { field ->
            when (field.type) {
                DynamicSchemaFieldType.STRING -> {
                    val input = AirwallexTextInputLayout(requireContext(), null)
                    input.tag = field
                    input.setHint(field.displayName)
                    when (field.uiType) {
                        DynamicSchemaFieldUIType.TEXT -> {
                            input.setInputType(InputType.TYPE_CLASS_TEXT)
                        }
                        DynamicSchemaFieldUIType.EMAIL -> {
                            input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                        }
                        DynamicSchemaFieldUIType.PHONE -> {
                            input.setKeyListener(DigitsKeyListener.getInstance("0123456789"))
                        }
                        else -> Unit
                    }

                    input.afterTextChanged {
                        input.error = null
                    }

                    input.afterFocusChanged { hasFocus ->
                        if (!hasFocus && isInvalid(input.value, field)) {
                            input.error = getString(R.string.airwallex_invalid_field, field.displayName.lowercase())
                        } else {
                            input.error = null
                        }
                    }

                    binding.content.addView(
                        input,
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                            topMargin =
                                resources.getDimension(R.dimen.airwallex_marginTop_20).toInt()
                        }
                    )
                }
                DynamicSchemaFieldType.ENUM -> {
                    val dynamicFieldView = DynamicFieldCompleteView(
                        requireContext(),
                        null,
                        field.candidates ?: emptyList()
                    )
                    dynamicFieldView.tag = field
                    dynamicFieldView.setHint(field.displayName)

                    binding.content.addView(
                        dynamicFieldView,
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                            topMargin =
                                resources.getDimension(R.dimen.airwallex_marginTop_20).toInt()
                        }
                    )
                }
                else -> Unit
            }
        }

        for (i in 0 until binding.content.childCount) {
            val childView = binding.content.getChildAt(i)
            if (i == binding.content.childCount - 1) {
                if (childView is AirwallexTextInputLayout) {
                    childView.setImeOptions(EditorInfo.IME_ACTION_DONE)
                } else if (childView is DynamicFieldCompleteView) {
                    childView.setImeOptions(EditorInfo.IME_ACTION_DONE)
                }
            } else {
                if (childView is AirwallexTextInputLayout) {
                    childView.setImeOptions(EditorInfo.IME_ACTION_NEXT)
                } else if (childView is DynamicFieldCompleteView) {
                    childView.setImeOptions(EditorInfo.IME_ACTION_NEXT)
                }
            }
        }

        binding.checkout.setOnSingleClickListener {
            val fieldMap = mutableMapOf<String, String>()
            for (i in 0 until binding.content.childCount) {
                val childView = binding.content.getChildAt(i)
                val field = childView.tag as DynamicSchemaField

                if (childView is AirwallexTextInputLayout) {
                    if (isInvalid(childView.value, field)) {
                        childView.error = getString(R.string.airwallex_invalid_field, field.displayName.lowercase())
                        return@setOnSingleClickListener
                    }
                    field.let {
                        fieldMap[it.name] = childView.value
                    }
                } else if (childView is DynamicFieldCompleteView) {
                    val value = childView.value
                    if (value == null || isInvalid(value, field)) {
                        childView.error = getString(R.string.airwallex_invalid_field, field.displayName.lowercase())
                        return@setOnSingleClickListener
                    }
                    field.let {
                        fieldMap[it.name] = value
                    }
                }
            }
            onCompleted?.invoke(fieldMap)
        }
    }

    private fun isInvalid(value: String, field: DynamicSchemaField): Boolean {
        val validations = field.validations
        val isNotMatchValidations = validations != null && isInvalid(value, validations)
        val isEmpty = value.isEmpty()
        return isNotMatchValidations || isEmpty
    }

    private fun isInvalid(
        text: String,
        validations: DynamicSchemaFieldValidation
    ): Boolean {
        val regex = validations.regex
        val max = validations.max

        return regex != null && !Regex(regex).matches(text) ||
            max != null && text.length > max
    }

    override fun onStart() {
        super.onStart()
        BottomSheetBehavior.from(requireView().parent as View).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
    }

    override fun bindFragment(
        inflater: LayoutInflater,
        container: ViewGroup
    ): DialogPaymentInfoBinding {
        return DialogPaymentInfoBinding.inflate(inflater, container, true)
    }
}
