package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.R
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.databinding.ActivityAddCardBinding

/**
 * Activity to add new payment method
 */
internal class AddPaymentMethodActivity : AirwallexCheckoutBaseActivity() {

    private val viewBinding: ActivityAddCardBinding by lazy {
        viewStub.layoutResource = R.layout.activity_add_card
        val root = viewStub.inflate() as ViewGroup
        ActivityAddCardBinding.bind(root)
    }

    private val args: AddPaymentMethodActivityLaunch.Args by lazy {
        AddPaymentMethodActivityLaunch.Args.getExtra(intent)
    }

    override val session: AirwallexSession by lazy {
        args.session
    }

    private val supportedCardSchemes: List<CardScheme> by lazy {
        args.supportedCardSchemes
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
                application, airwallex, session, supportedCardSchemes
            )
        )[AddPaymentMethodViewModel::class.java]
    }

    private fun onSaveCard() {
        val card = viewBinding.cardWidget.paymentMethodCard ?: return
        setLoadingProgress(loading = true, cancelable = false)
        val observer = Observer<AirwallexPaymentStatus> { result ->
            when (result) {
                is AirwallexPaymentStatus.Success -> {
                    finishWithPaymentIntent(paymentIntentId = result.paymentIntentId)
                }
                is AirwallexPaymentStatus.Failure -> {
                    finishWithPaymentIntent(exception = result.exception)
                }
                else -> Unit
            }
        }

        val shouldStoreCard = viewBinding.swSaveCard.isChecked
        viewModel.createPaymentMethod(
            card,
            shouldStoreCard,
            viewBinding.billingWidget.billing
        ).observe(this) {
            startPaymentWithMethod(it, shouldStoreCard, observer)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        airwallex.handlePaymentData(requestCode, resultCode, data)
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    private fun startPaymentWithMethod(
        result: AddPaymentMethodViewModel.PaymentMethodResult,
        shouldStoreCard: Boolean,
        observer: Observer<AirwallexPaymentStatus>
    ) {
        when (result) {
            is AddPaymentMethodViewModel.PaymentMethodResult.Success -> {
                startCheckout(
                    paymentMethod = result.paymentMethod,
                    cvc = result.cvc,
                    observer = observer,
                    saveCard = shouldStoreCard
                )
            }
            is AddPaymentMethodViewModel.PaymentMethodResult.Error -> {
                finishWithPaymentIntent(exception = result.exception)
            }
        }
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
        viewBinding.btnSaveCard.isEnabled = isValid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.cardWidget.showEmail = session.isEmailRequired
        viewBinding.cardWidget.validationMessageCallback = { cardNumber ->
            when (val result = viewModel.getValidationResult(cardNumber)) {
                is AddPaymentMethodViewModel.ValidationResult.Success -> {
                    null
                }
                is AddPaymentMethodViewModel.ValidationResult.Error -> {
                    resources.getString(result.message)
                }
            }
        }
        viewBinding.cardWidget.cardChangeCallback = { invalidateConfirmStatus() }
        if (session is AirwallexPaymentSession && session.customerId != null) {
            viewBinding.saveCardWidget.visibility = View.VISIBLE
        } else {
            viewBinding.saveCardWidget.visibility = View.GONE
        }
        viewBinding.billingWidget.shipping = shipping
        viewBinding.billingWidget.billingChangeCallback = { invalidateConfirmStatus() }

        viewBinding.btnSaveCard.isEnabled = isValid
        viewBinding.btnSaveCard.setOnSingleClickListener { onSaveCard() }

        viewBinding.billingGroup.visibility = if (session.isBillingInformationRequired) View.VISIBLE else View.GONE
    }
}
