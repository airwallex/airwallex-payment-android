package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.*
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.Shipping
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.Airwallex
import com.airwallex.android.R
import com.airwallex.android.databinding.ActivityAddCardBinding
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethodType

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

    override fun onActionSave() {
        val card = viewBinding.cardWidget.paymentMethodCard ?: return
        setLoadingProgress(loading = true, cancelable = false)

        if (session is AirwallexPaymentSession) {
            startCheckout(
                paymentMethod = PaymentMethod(
                    type = PaymentMethodType.CARD,
                    card = card,
                    billing = viewBinding.billingWidget.billing
                ),
                observer = {
                    when (it) {
                        is AirwallexCheckoutViewModel.PaymentResult.Success -> {
                            finishWithPaymentIntent(paymentIntent = it.paymentIntent)
                        }
                        is AirwallexCheckoutViewModel.PaymentResult.Error -> {
                            finishWithPaymentIntent(exception = it.exception)
                        }
                        else -> Unit
                    }
                }
            )
        } else {
            viewModel.createPaymentMethod(card, viewBinding.billingWidget.billing).observe(
                this,
                {
                    when (it) {
                        is AddPaymentMethodViewModel.PaymentMethodResult.Success -> {
                            finishWithPaymentMethod(it.paymentMethod, requireNotNull(it.cvc))
                        }
                        is AddPaymentMethodViewModel.PaymentMethodResult.Error -> {
                            alertError(it.exception.message ?: it.exception.toString())
                        }
                    }
                }
            )
        }
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    private fun alertError(error: String) {
        setLoadingProgress(false)
        alert(message = error)
    }

    private fun finishWithPaymentIntent(
        paymentIntent: PaymentIntent? = null,
        exception: Exception? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                AddPaymentMethodActivityLaunch.Result(
                    paymentIntent = paymentIntent,
                    exception = exception
                ).toBundle()
            )
        )
        finish()
    }

    private fun finishWithPaymentMethod(paymentMethod: PaymentMethod, cvc: String) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                AddPaymentMethodActivityLaunch.Result(
                    paymentMethod = paymentMethod,
                    cvc = cvc
                ).toBundle()
            )
        )
        finish()
    }

    private fun invalidateConfirmStatus() {
        if (isValid) {
            viewBinding.tvSaveCard.isEnabled = true
            keyboardController.hide()
        } else {
            viewBinding.tvSaveCard.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.cardWidget.cardChangeCallback = { invalidateConfirmStatus() }
        viewBinding.billingWidget.shipping = shipping
        viewBinding.billingWidget.billingChangeCallback = { invalidateConfirmStatus() }

        viewBinding.tvSaveCard.isEnabled = isValid
        viewBinding.tvSaveCard.setOnClickListener { onActionSave() }
    }
}
