/**************************************************************************************************
 * Copyright VaxCare (c) 2020.                                                                    *
 **************************************************************************************************/

package com.airwallex.android.view

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout.LayoutParams
import com.airwallex.android.R
import com.airwallex.android.databinding.DialogPaymentInfoBinding
import com.airwallex.android.model.PaymentMethodType

class PaymentInfoBottomSheetDialog : BottomSheetDialog() {

    var onCompleted: ((name: String?, email: String?, phone: String?) -> Unit)? = null

    companion object {
        private const val PAYMENT_METHOD_TYPE = "payment_method_type"
        private const val TITLE = "title"
        private const val WITH_NAME = "with_name"
        private const val WITH_EMAIL = "with_email"
        private const val WITH_PHONE = "with_phone"

        fun newInstance(
            paymentMethodType: PaymentMethodType,
            title: String,
            withName: Boolean = true,
            withEmail: Boolean = true,
            withPhone: Boolean = true
        ): PaymentInfoBottomSheetDialog {
            val args = Bundle()
            args.putParcelable(PAYMENT_METHOD_TYPE, paymentMethodType)
            args.putString(TITLE, title)
            args.putBoolean(WITH_NAME, withName)
            args.putBoolean(WITH_EMAIL, withEmail)
            args.putBoolean(WITH_PHONE, withPhone)
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

        if (arguments?.getBoolean(WITH_NAME) == true) {
            nameInput = AirwallexTextInputLayout(requireContext(), null)
            nameInput.afterTextChanged { nameInput.error = null }
            nameInput.setHint(getString(R.string.shopper_name_hint))
            nameInput.setInputType(InputType.TYPE_CLASS_TEXT)

            viewBinding.content.addView(
                nameInput,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    topMargin = resources.getDimension(R.dimen.marginTop_20).toInt()
                }
            )
        }

        if (arguments?.getBoolean(WITH_EMAIL) == true) {
            emailInput = AirwallexTextInputLayout(requireContext(), null)
            emailInput.afterTextChanged { emailInput.error = null }
            emailInput.setHint(getString(R.string.shopper_email_hint))
            emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            viewBinding.content.addView(
                emailInput,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    topMargin = resources.getDimension(R.dimen.marginTop_20).toInt()
                }
            )
        }

        if (arguments?.getBoolean(WITH_PHONE) == true) {
            phoneInput = AirwallexTextInputLayout(requireContext(), null)
            phoneInput.afterTextChanged { phoneInput.error = null }
            phoneInput.setHint(getString(R.string.shopper_phone_hint))
            phoneInput.setInputType(InputType.TYPE_CLASS_PHONE)
            viewBinding.content.addView(
                phoneInput,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    topMargin = resources.getDimension(R.dimen.marginTop_20).toInt()
                }
            )
        }

        for (i in 0 until viewBinding.content.childCount) {
            if (i == viewBinding.content.childCount - 1) {
                (viewBinding.content.getChildAt(i) as AirwallexTextInputLayout).setImeOptions(EditorInfo.IME_ACTION_DONE)
            } else {
                (viewBinding.content.getChildAt(i) as AirwallexTextInputLayout).setImeOptions(EditorInfo.IME_ACTION_NEXT)
            }
        }

        viewBinding.checkout.setOnClickListener {

            // Name
            if (nameInput != null && nameInput.value.isEmpty()) {
                nameInput.error = getString(R.string.payment_method_filed_empty_error)
                return@setOnClickListener
            }

            when (arguments?.getParcelable<PaymentMethodType>(PAYMENT_METHOD_TYPE)) {
                PaymentMethodType.SKRILL -> {
                    if (nameInput != null && nameInput.value.split(" ").size != 2) {
                        nameInput.error = getString(R.string.invalid_name)
                        return@setOnClickListener
                    }
                }
                else -> {
                    if (nameInput != null && nameInput.value.length < 3) {
                        nameInput.error = getString(R.string.name_length_short_error)
                        return@setOnClickListener
                    }
                    if (nameInput != null && nameInput.value.length > 100) {
                        nameInput.error = getString(R.string.name_length_long_error)
                        return@setOnClickListener
                    }
                }
            }

            // Email
            if (emailInput != null && emailInput.value.isEmpty()) {
                emailInput.error = getString(R.string.payment_method_filed_empty_error)
                return@setOnClickListener
            }
            if (emailInput != null && !Patterns.EMAIL_ADDRESS.matcher(emailInput.value).matches()) {
                emailInput.error = getString(R.string.invalid_email_address)
                return@setOnClickListener
            }

            // Phone
            if (phoneInput != null && phoneInput.value.isEmpty()) {
                phoneInput.error = getString(R.string.payment_method_filed_empty_error)
                return@setOnClickListener
            }

            when (arguments?.getParcelable<PaymentMethodType>(PAYMENT_METHOD_TYPE)) {
                PaymentMethodType.PAY_EASY -> {
                    if (phoneInput != null && (phoneInput.value.length < 10 || phoneInput.value.length > 11)) {
                        phoneInput.error = getString(R.string.phone_error)
                        return@setOnClickListener
                    }
                }
                else -> Unit
            }
            onCompleted?.invoke(nameInput?.value, emailInput?.value, phoneInput?.value)
        }
    }
}
