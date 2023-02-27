package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.R
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.databinding.ActivityAddCardBinding
import kotlinx.coroutines.launch

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

    private val isSinglePaymentMethod: Boolean by lazy {
        args.isSinglePaymentMethod
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

    private var currentBrand: CardBrand? = null

    override fun onBackPressed() {
        setResult(
            Activity.RESULT_CANCELED,
            Intent().putExtras(
                AddPaymentMethodActivityLaunch.CancellationResult(
                    isSinglePaymentMethod = isSinglePaymentMethod
                ).toBundle()
            )
        )
        super.onBackPressed()
    }

    private fun onSaveCard() {
        val card = viewBinding.cardWidget.paymentMethodCard ?: return
        val resultHandler: (AirwallexPaymentStatus) -> Unit = { result ->
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

        if (viewBinding.swSaveCard.isChecked) {
            lifecycleScope.launch {
                setLoadingProgress(loading = true, cancelable = false)
                try {
                    val result = viewModel.checkoutWithSavedCard(card, viewBinding.billingWidget.billing)
                    resultHandler(result)
                } catch (e: AirwallexException) {
                    finishWithPaymentIntent(exception = e)
                }
            }
        } else {
            setLoadingProgress(loading = true, cancelable = false)
            val observer = Observer(resultHandler)
            viewModel.createPaymentMethod(
                card,
                viewBinding.billingWidget.billing
            ).observe(this) {
                startPaymentWithMethod(it, observer)
            }
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
        observer: Observer<AirwallexPaymentStatus>
    ) {
        when (result) {
            is AddPaymentMethodViewModel.PaymentMethodResult.Success -> {
                startCheckout(
                    paymentMethod = result.paymentMethod,
                    cvc = result.cvc,
                    observer = observer
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

    private fun showUnionPayWarning() {
        viewBinding.warningView.message = getString(R.string.airwallex_save_union_pay_card)
        viewBinding.warningView.visibility = View.VISIBLE
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
        viewBinding.cardWidget.brandChangeCallback = { cardBrand ->
            currentBrand = cardBrand
            if (cardBrand == CardBrand.UnionPay && viewBinding.swSaveCard.isChecked) {
                showUnionPayWarning()
            } else {
                viewBinding.warningView.visibility = View.GONE
            }
        }
        viewBinding.cardWidget.cardChangeCallback = { invalidateConfirmStatus() }
        if (session is AirwallexPaymentSession && session.customerId != null) {
            viewBinding.saveCardWidget.visibility = View.VISIBLE
            viewBinding.swSaveCard.setOnCheckedChangeListener { _, isChecked ->
                if (currentBrand == CardBrand.UnionPay && isChecked) {
                    showUnionPayWarning()
                } else {
                    viewBinding.warningView.visibility = View.GONE
                }
            }
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
