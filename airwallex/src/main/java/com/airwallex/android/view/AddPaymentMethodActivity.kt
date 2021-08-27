package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.databinding.ActivityAddCardBinding
import com.airwallex.android.R
import com.airwallex.android.core.extension.setOnSingleClickListener

/**
 * Activity to add new payment method
 */
internal class AddPaymentMethodActivity : AirwallexCheckoutBaseActivity() {

    private val viewBinding: ActivityAddCardBinding by lazy {
        viewStub.layoutResource = R.layout.activity_add_card
        val root = viewStub.inflate() as ViewGroup
        ActivityAddCardBinding.bind(root)
    }

    private val keyboardController: KeyboardController by lazy {
        KeyboardController(this)
    }

    private val args: AddPaymentMethodActivityLaunch.Args by lazy {
        AddPaymentMethodActivityLaunch.Args.getExtra(intent)
    }

    override val session: AirwallexSession by lazy {
        args.session
    }

    private val shipping: Shipping? by lazy {
        when (session) {
            is AirwallexPaymentSession -> {
                (session as AirwallexPaymentSession).paymentIntent.order?.shipping
            }
            is AirwallexRecurringWithIntentSession -> {
                (session as AirwallexRecurringWithIntentSession).paymentIntent.order?.shipping
            }
            is AirwallexRecurringSession -> {
                (session as AirwallexRecurringSession).shipping
            }
            else -> null
        }
    }

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    private val isValid: Boolean
        get() {
            return viewBinding.cardWidget.isValid && viewBinding.billingWidget.isValid
        }

    private val viewModel: AddPaymentMethodViewModel by lazy {
        ViewModelProvider(
            this,
            AddPaymentMethodViewModel.Factory(
                application, airwallex, session
            )
        )[AddPaymentMethodViewModel::class.java]
    }

    private fun onSaveCard() {
        val card = viewBinding.cardWidget.paymentMethodCard ?: return
        setLoadingProgress(loading = true, cancelable = false)
        val observer = Observer<AirwallexCheckoutViewModel.PaymentResult> {
            when (it) {
                is AirwallexCheckoutViewModel.PaymentResult.Success -> {
                    finishWithPaymentIntent(paymentIntentId = it.paymentIntentId)
                }
                is AirwallexCheckoutViewModel.PaymentResult.Error -> {
                    finishWithPaymentIntent(exception = it.exception)
                }
                else -> Unit
            }
        }

        if (session is AirwallexPaymentSession) {
            startCheckout(
                paymentMethod = PaymentMethod.Builder()
                    .setType(PaymentMethodType.CARD)
                    .setCard(card)
                    .setBilling(viewBinding.billingWidget.billing)
                    .build(),
                observer = observer
            )
        } else {
            viewModel.createPaymentMethod(card, viewBinding.billingWidget.billing).observe(
                this,
                {
                    when (it) {
                        is AddPaymentMethodViewModel.PaymentMethodResult.Success -> {
                            startCheckout(
                                paymentMethod = it.paymentMethod,
                                cvc = it.cvc,
                                observer = observer
                            )
                        }
                        is AddPaymentMethodViewModel.PaymentMethodResult.Error -> {
                            finishWithPaymentIntent(exception = it.exception)
                        }
                    }
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        airwallex.handlePaymentData(requestCode, resultCode, data)
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    private fun finishWithPaymentIntent(
        paymentIntentId: String? = null,
        exception: AirwallexException? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                AddPaymentMethodActivityLaunch.Result(
                    paymentIntentId = paymentIntentId,
                    exception = exception
                ).toBundle()
            )
        )
        finish()
    }

    private fun invalidateConfirmStatus() {
        if (isValid) {
            viewBinding.btnSaveCard.isEnabled = true
            keyboardController.hide()
        } else {
            viewBinding.btnSaveCard.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.cardWidget.cardChangeCallback = { invalidateConfirmStatus() }
        viewBinding.billingWidget.shipping = shipping
        viewBinding.billingWidget.billingChangeCallback = { invalidateConfirmStatus() }

        viewBinding.btnSaveCard.isEnabled = isValid
        viewBinding.btnSaveCard.setOnSingleClickListener { onSaveCard() }
    }
}
