package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.databinding.ActivityAddCardBinding
import com.airwallex.android.ui.extension.getExtraArgs
import com.airwallex.risk.AirwallexRisk
import kotlinx.coroutines.launch

/**
 * Activity to add new payment method
 */
internal class AddPaymentMethodActivity : AirwallexCheckoutBaseActivity(), TrackablePage {

    private val viewBinding: ActivityAddCardBinding by lazy {
        viewStub.layoutResource = R.layout.activity_add_card
        val root = viewStub.inflate() as ViewGroup
        ActivityAddCardBinding.bind(root)
    }

    private val args: AddPaymentMethodActivityLaunch.Args by lazy {
        intent.getExtraArgs()
    }

    override val pageName: String
        get() = viewModel.pageName

    override val additionalInfo: Map<String, Any>
        get() = viewModel.additionalInfo

    override val session: AirwallexSession by lazy {
        args.session
    }

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    private val viewModel: AddPaymentMethodViewModel by lazy {
        ViewModelProvider(
            this, AddPaymentMethodViewModel.Factory(
                application, airwallex, session, args.supportedCardSchemes
            )
        )[AddPaymentMethodViewModel::class.java]
    }

    private val isValid: Boolean
        get() {
            return viewBinding.cardWidget.isValid && viewBinding.billingWidget.isValid
        }

    private var currentBrand: CardBrand? = null

    override fun initView() {
        super.initView()
        AirwallexRisk.log(event = "show_create_card", screen = "page_create_card")
        viewBinding.cardWidget.showEmail = viewModel.isEmailRequired
        viewBinding.billingWidget.shipping = viewModel.shipping
        viewBinding.btnSaveCard.text = viewModel.ctaTitle
        viewBinding.btnSaveCard.isEnabled = isValid
        viewBinding.billingGroup.visibility =
            if (viewModel.isBillingRequired) View.VISIBLE else View.GONE
        viewBinding.saveCardWidget.visibility =
            if (viewModel.canSaveCard) View.VISIBLE else View.GONE
    }

    override fun addListener() {
        super.addListener()
        viewBinding.cardWidget.validationMessageCallback = { cardNumber ->
            when (val result = viewModel.getValidationResult(cardNumber)) {
                is AddPaymentMethodViewModel.ValidationResult.Success -> null
                is AddPaymentMethodViewModel.ValidationResult.Error -> resources.getString(result.message)
            }
        }
        viewBinding.cardWidget.brandChangeCallback = { cardBrand ->
            currentBrand = cardBrand
            handleUnionPayWarning()
        }
        viewBinding.cardWidget.cardChangeCallback = { invalidateConfirmStatus() }
        viewBinding.billingWidget.billingChangeCallback = {
            invalidateConfirmStatus()
            AnalyticsLogger.logAction("toggle_billing_address")
        }
        viewBinding.btnSaveCard.setOnSingleClickListener { onSaveCard() }
        viewBinding.swSaveCard.setOnCheckedChangeListener { _, isChecked ->
            handleUnionPayWarning()
            if (isChecked) {
                AnalyticsLogger.logAction("save_card")
            }
        }
    }

    override fun onBackButtonPressed() {
        AirwallexLogger.info("AddPaymentMethodActivity onBackButtonPressed")
        setResult(
            Activity.RESULT_CANCELED, Intent().putExtras(
                AddPaymentMethodActivityLaunch.CancellationResult(
                    isSinglePaymentMethod =  args.isSinglePaymentMethod
                ).toBundle()
            )
        )
        finish()
    }

    private fun invalidateConfirmStatus() {
        viewBinding.btnSaveCard.isEnabled = isValid
    }

    private fun handleUnionPayWarning() {
        if (currentBrand == CardBrand.UnionPay && viewBinding.swSaveCard.isChecked) {
            viewBinding.warningView.message = getString(R.string.airwallex_save_union_pay_card)
            viewBinding.warningView.visibility = View.VISIBLE
        } else {
            viewBinding.warningView.visibility = View.GONE
        }
    }

    private fun onSaveCard() {
        AnalyticsLogger.logAction("tap_pay_button")
        AirwallexRisk.log(event = "click_payment_button", screen = "page_create_card")

        val card = viewBinding.cardWidget.paymentMethodCard ?: return
        val resultHandler: (AirwallexPaymentStatus) -> Unit = { result ->
            when (result) {
                is AirwallexPaymentStatus.Success -> {
                    finishWithPaymentIntent(
                        paymentIntentId = result.paymentIntentId, consentId = result.consentId
                    )
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
                    val result =
                        viewModel.checkoutWithSavedCard(card, viewBinding.billingWidget.billing)
                    resultHandler(result)
                } catch (e: AirwallexException) {
                    finishWithPaymentIntent(exception = e)
                }
            }
        } else {
            setLoadingProgress(loading = true, cancelable = false)
            val observer = Observer(resultHandler)
            viewModel.createPaymentMethod(
                card, viewBinding.billingWidget.billing
            ).observe(this) {
                startPaymentWithMethod(it, observer)
            }
        }
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
                    paymentMethod = result.paymentMethod, cvc = result.cvc, observer = observer
                )
            }

            is AddPaymentMethodViewModel.PaymentMethodResult.Error -> {
                finishWithPaymentIntent(exception = result.exception)
            }
        }
    }

    private fun finishWithPaymentIntent(
        paymentIntentId: String? = null,
        consentId: String? = null,
        exception: AirwallexException? = null
    ) {
        setLoadingProgress(false)
        AirwallexLogger.info("AddPaymentMethodActivity finishWithPaymentIntent")
        setResult(
            Activity.RESULT_OK, Intent().putExtras(
                AddPaymentMethodActivityLaunch.Result(
                    paymentIntentId = paymentIntentId, consentId = consentId, exception = exception
                ).toBundle()
            )
        )
        finish()
    }
}
