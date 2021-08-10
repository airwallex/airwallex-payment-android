package com.airwallex.android.ui.view

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout.LayoutParams
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.model.PaymentMethodRequiredField
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.R
import com.airwallex.android.ui.databinding.DialogPaymentInfoBinding

class PaymentInfoBottomSheetDialog : BottomSheetDialog() {

    var onCompleted: ((name: String?, email: String?, phone: String?) -> Unit)? = null

    companion object {
        private const val PAYMENT_METHOD_TYPE = "payment_method_type"
        private const val TITLE = "title"
        private const val REQUIRED_FIELDS = "required_fields"

        fun newInstance(
            paymentMethodType: PaymentMethodType,
            title: String,
            requiredFields: List<PaymentMethodRequiredField>
        ): PaymentInfoBottomSheetDialog {
            val args = Bundle()
            args.putParcelable(PAYMENT_METHOD_TYPE, paymentMethodType)
            args.putString(TITLE, title)
            args.putParcelableArrayList(REQUIRED_FIELDS, ArrayList(requiredFields))
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

        viewBinding.title.text = arguments?.getString(TITLE)

        var nameInput: AirwallexTextInputLayout? = null
        var emailInput: AirwallexTextInputLayout? = null
        var phoneInput: AirwallexTextInputLayout? = null

        val requiredFields =
            arguments?.getParcelableArrayList<PaymentMethodRequiredField>(REQUIRED_FIELDS)
        if (requiredFields?.any { it == PaymentMethodRequiredField.SHOPPER_NAME } == true) {
            nameInput = AirwallexTextInputLayout(requireContext(), null)
            nameInput.afterTextChanged { nameInput.error = null }
            nameInput.setHint(getString(R.string.airwallex_shopper_name_hint))
            nameInput.setInputType(InputType.TYPE_CLASS_TEXT)

            viewBinding.content.addView(
                nameInput,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    topMargin = resources.getDimension(R.dimen.airwallex_marginTop_20).toInt()
                }
            )
        }

        if (requiredFields?.any { it == PaymentMethodRequiredField.SHOPPER_EMAIL } == true) {
            emailInput = AirwallexTextInputLayout(requireContext(), null)
            emailInput.afterTextChanged { emailInput.error = null }
            emailInput.setHint(getString(R.string.airwallex_shopper_email_hint))
            emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            viewBinding.content.addView(
                emailInput,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    topMargin = resources.getDimension(R.dimen.airwallex_marginTop_20).toInt()
                }
            )
        }

        if (requiredFields?.any { it == PaymentMethodRequiredField.SHOPPER_PHONE } == true) {
            phoneInput = AirwallexTextInputLayout(requireContext(), null)
            phoneInput.afterTextChanged { phoneInput.error = null }
            phoneInput.setHint(getString(R.string.airwallex_shopper_phone_hint))
            phoneInput.setInputType(InputType.TYPE_CLASS_PHONE)
            viewBinding.content.addView(
                phoneInput,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    topMargin = resources.getDimension(R.dimen.airwallex_marginTop_20).toInt()
                }
            )
        }

        for (i in 0 until viewBinding.content.childCount) {
            if (i == viewBinding.content.childCount - 1) {
                (viewBinding.content.getChildAt(i) as AirwallexTextInputLayout).setImeOptions(
                    EditorInfo.IME_ACTION_DONE
                )
            } else {
                (viewBinding.content.getChildAt(i) as AirwallexTextInputLayout).setImeOptions(
                    EditorInfo.IME_ACTION_NEXT
                )
            }
        }

        viewBinding.checkout.setOnSingleClickListener {
            // Name
            if (nameInput != null && nameInput.value.isEmpty()) {
                nameInput.error = getString(R.string.airwallex_payment_method_filed_empty_error)
                return@setOnSingleClickListener
            }

            when (arguments?.getParcelable<PaymentMethodType>(PAYMENT_METHOD_TYPE)) {
                PaymentMethodType.SKRILL -> {
                    if (nameInput != null && nameInput.value.split(" ").size != 2) {
                        nameInput.error = getString(R.string.airwallex_invalid_name)
                        return@setOnSingleClickListener
                    }
                }
                else -> {
                    if (nameInput != null && nameInput.value.length < 3) {
                        nameInput.error = getString(R.string.airwallex_name_length_short_error)
                        return@setOnSingleClickListener
                    }
                    if (nameInput != null && nameInput.value.length > 100) {
                        nameInput.error = getString(R.string.airwallex_name_length_long_error)
                        return@setOnSingleClickListener
                    }
                }
            }

            // Email
            if (emailInput != null && emailInput.value.isEmpty()) {
                emailInput.error = getString(R.string.airwallex_payment_method_filed_empty_error)
                return@setOnSingleClickListener
            }
            if (emailInput != null && !Patterns.EMAIL_ADDRESS.matcher(emailInput.value).matches()) {
                emailInput.error = getString(R.string.airwallex_invalid_email_address)
                return@setOnSingleClickListener
            }

            // Phone
            if (phoneInput != null && phoneInput.value.isEmpty()) {
                phoneInput.error = getString(R.string.airwallex_payment_method_filed_empty_error)
                return@setOnSingleClickListener
            }

            when (arguments?.getParcelable<PaymentMethodType>(PAYMENT_METHOD_TYPE)) {
                PaymentMethodType.PAY_EASY -> {
                    if (phoneInput != null && (phoneInput.value.length < 10 || phoneInput.value.length > 11)) {
                        phoneInput.error = getString(R.string.airwallex_phone_error)
                        return@setOnSingleClickListener
                    }
                }
                else -> Unit
            }
            onCompleted?.invoke(nameInput?.value, emailInput?.value, phoneInput?.value)
        }
    }
}
