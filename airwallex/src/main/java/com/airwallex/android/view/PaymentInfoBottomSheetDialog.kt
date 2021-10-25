package com.airwallex.android.view

import android.os.Bundle
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Patterns
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout.LayoutParams
import android.widget.Toast
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.databinding.DialogPaymentInfoBinding
import com.airwallex.android.R
import com.airwallex.android.core.model.*

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
            ?.firstOrNull { schema -> schema.transactionMode == TransactionMode.ONE_OFF }
            ?.fields
            ?.filter {
                it.type != DynamicSchemaFieldType.BANKS && !it.hidden
            }
            ?: return

        viewBinding.title.text = paymentMethodTypeInfo.displayName

        fields.forEach { field ->
            when (field.type) {
                DynamicSchemaFieldType.STRING -> {
                    val input = AirwallexTextInputLayout(requireContext(), null)
                    input.tag = field.name
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

                    viewBinding.content.addView(
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
                    dynamicFieldView.tag = field.name
                    dynamicFieldView.setHint(field.displayName)

                    viewBinding.content.addView(
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

        for (i in 0 until viewBinding.content.childCount) {
            val childView = viewBinding.content.getChildAt(i)
            if (i == viewBinding.content.childCount - 1) {
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

        viewBinding.checkout.setOnSingleClickListener {
            val fieldMap = mutableMapOf<String, String>()
            for (i in 0 until viewBinding.content.childCount) {
                val childView = viewBinding.content.getChildAt(i)
                val field = fields.find { it.name == childView.tag }

                if (childView is AirwallexTextInputLayout) {
                    val validations = field?.validations
                    if ((validations != null && isInvalid(childView.value, validations)) ||
                        childView.value.isEmpty() ||
                        (field?.uiType == DynamicSchemaFieldUIType.EMAIL && !Patterns.EMAIL_ADDRESS.matcher(childView.value).matches())
                    ) {
                        Toast.makeText(
                            context,
                            getString(R.string.invalid_field, field?.displayName ?: ""),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnSingleClickListener
                    }
                    field?.let {
                        fieldMap[it.name] = childView.value
                    }
                } else if (childView is DynamicFieldCompleteView) {
                    val value = childView.value
                    if (value.isNullOrEmpty()) {
                        Toast.makeText(
                            context,
                            getString(R.string.invalid_field, field?.displayName ?: ""),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnSingleClickListener
                    }
                    field?.let {
                        fieldMap[it.name] = value
                    }
                }
            }
            onCompleted?.invoke(fieldMap)
        }
    }

    private fun isInvalid(
        text: String,
        Validations: DynamicSchemaFieldValidation
    ): Boolean {
        val regex = Validations.regex
        val max = Validations.max

        return regex != null && !Regex(regex).matches(text) ||
            max != null && text.length > max
    }
}
