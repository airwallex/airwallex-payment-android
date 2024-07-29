package com.airwallex.android.view

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.AirwallexSupportedCard
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.databinding.DialogAddCardBinding
import com.airwallex.risk.AirwallexRisk
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("InflateParams")
class AirwallexAddPaymentDialog(
    private val activity: ComponentActivity,
    private val session: AirwallexSession,
    private val supportedCardSchemes: List<CardScheme> =
        enumValues<AirwallexSupportedCard>().toList().map { CardScheme(it.brandName) },
    private val paymentResultListener: Airwallex.PaymentResultListener,
    private val dialogHeight: Int? = null,
    private val paymentFlowProvider: Airwallex.PaymentFlowProvider? = null
) : BottomSheetDialog(activity), TrackablePage {

    private val viewBinding: DialogAddCardBinding by lazy {
        val root = layoutInflater.inflate(R.layout.dialog_add_card, null, false)
        DialogAddCardBinding.bind(root)
    }

    override val pageName: String
        get() = viewModel.pageName

    override val additionalInfo: Map<String, Any>
        get() = viewModel.additionalInfo

    private val viewModel: AddPaymentMethodViewModel by lazy {
        createViewModel()
    }

    private val viewModelStore = ViewModelStore()
    private fun createViewModel(): AddPaymentMethodViewModel {
        val factoryKey = System.currentTimeMillis().toString()
        return ViewModelProvider(
            viewModelStore,
            AddPaymentMethodViewModel.Factory(
                (activity.applicationContext as Application),
                Airwallex(activity),
                session,
                supportedCardSchemes
            )
        )[factoryKey, AddPaymentMethodViewModel::class.java]
    }

    private val isValid: Boolean
        get() {
            val isCardValid = viewBinding.cardWidget.isValid
            var isBillingValid = true
            if (viewBinding.billingGroup.visibility == View.VISIBLE) {
                isBillingValid = viewBinding.billingWidget.isValid
            }
            return isCardValid && isBillingValid
        }

    private var currentBrand: CardBrand? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initDialog()
        initView()
        addListener()
    }

    private fun initDialog() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.apply {
            val behavior = BottomSheetBehavior.from(this)
            val layoutParams = this.layoutParams
            layoutParams.height =
                dialogHeight ?: (context.resources.displayMetrics.heightPixels * 0.9).toInt()
            this.layoutParams = layoutParams

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }
    }

    private fun initView() {
        AirwallexRisk.log(event = "show_create_card", screen = "page_create_card")
        viewBinding.closeIcon.setColorFilter(Color.BLACK)
        viewBinding.cardWidget.showEmail = viewModel.isEmailRequired
        viewBinding.billingWidget.shipping = viewModel.shipping
        viewBinding.btnSaveCard.text = viewModel.ctaTitle
        viewBinding.btnSaveCard.isEnabled = false
        viewBinding.billingGroup.visibility =
            if (viewModel.isBillingRequired) View.VISIBLE else View.GONE
        viewBinding.saveCardWidget.visibility =
            if (viewModel.canSaveCard) View.VISIBLE else View.GONE
    }

    private fun addListener() {
        viewBinding.cardWidget.validationMessageCallback = { cardNumber ->
            when (val result = viewModel.getValidationResult(cardNumber)) {
                is AddPaymentMethodViewModel.ValidationResult.Success -> null
                is AddPaymentMethodViewModel.ValidationResult.Error -> context.getString(result.message)
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
        viewBinding.closeIcon.setOnClickListener {
            cancelPayment()
        }
        viewModel.airwallexPaymentStatus.observe(activity) { result ->
            when (result) {
                is AirwallexPaymentStatus.Success -> {
                    dismissWithPaymentResult(
                        paymentIntentId = result.paymentIntentId,
                        paymentConsentId = result.consentId
                    )
                }

                is AirwallexPaymentStatus.Failure -> {
                    dismissWithPaymentResult(exception = result.exception)
                }

                else -> Unit
            }
        }
    }

    private fun invalidateConfirmStatus() {
        viewBinding.btnSaveCard.isEnabled = isValid
    }

    private fun handleUnionPayWarning() {
        if (currentBrand == CardBrand.UnionPay && viewBinding.swSaveCard.isChecked) {
            viewBinding.warningView.message =
                context.getString(R.string.airwallex_save_union_pay_card)
            viewBinding.warningView.visibility = View.VISIBLE
        } else {
            viewBinding.warningView.visibility = View.GONE
        }
    }

    private fun onSaveCard() {
        AnalyticsLogger.logAction("tap_pay_button")
        AirwallexRisk.log(event = "click_payment_button", screen = "page_create_card")
        val card = viewBinding.cardWidget.paymentMethodCard ?: return
        setLoadingProgress(loading = true)
        viewModel.confirmPayment(
            card,
            viewBinding.swSaveCard.isChecked,
            viewBinding.billingWidget.billing,
        )
    }

    private fun dismissWithPaymentResult(
        paymentIntentId: String? = null,
        paymentConsentId: String? = null,
        exception: AirwallexException? = null
    ) {
        setLoadingProgress(false)
        AirwallexLogger.info("AddPaymentMethodDialog dismissWithPaymentResult")
        when {
            exception != null -> {
                AirwallexLogger.error("AddPaymentMethodDialog handlePaymentData: failed", exception)
                paymentResultListener.onCompleted(
                    AirwallexPaymentStatus.Failure(exception)
                )
            }

            paymentIntentId != null -> {
                AirwallexLogger.info("AddPaymentMethodDialog handlePaymentData: success,")
                paymentResultListener.onCompleted(
                    AirwallexPaymentStatus.Success(
                        paymentIntentId,
                        paymentConsentId
                    )
                )
            }

            else -> {
                paymentResultListener.onCompleted(
                    AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "paymentIntentId result is null"))
                )
            }
        }
        dismiss()
    }

    private fun cancelPayment() {
        val paymentSession = session as? AirwallexPaymentSession
        AirwallexLogger.info("AddPaymentMethodDialog cancelPayment[${paymentSession?.paymentIntent?.id}]: cancel")
        paymentResultListener.onCompleted(AirwallexPaymentStatus.Cancel)
        dismiss()
    }

    private fun setLoadingProgress(loading: Boolean) {
        viewBinding.frLoading.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun dismiss() {
        super.dismiss()
        viewModelStore.clear()
    }
}