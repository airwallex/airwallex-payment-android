package com.airwallex.android.view

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.databinding.DialogAddCardBinding
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.view.composables.card.CardSection
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
) : BottomSheetDialog(activity, R.style.AirwallexBottomSheetDialog), TrackablePage {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        AirwallexRisk.log(AirwallexRisk.Events.TRANSACTION_INITIATED, "")
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
                dialogHeight ?: (context.resources.displayMetrics.heightPixels * 0.65).toInt()
            this.layoutParams = layoutParams

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun initView() {
        AirwallexRisk.log(event = "show_create_card", screen = "page_create_card")
        viewBinding.closeIcon.setColorFilter(Color.BLACK)
        viewBinding.composeView.apply {
            setContent {
                AirwallexTheme {
                    CardSection(
                        addPaymentMethodViewModel = viewModel,
                        cardSchemes = supportedCardSchemes,
                        onAddCard = {
                            AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to PaymentMethodType.CARD.value))
                            setLoadingProgress(true)
                        },
                        onDeleteCard = {},
                        onCheckoutWithoutCvc = {},
                        onCheckoutWithCvc = { _, _ -> },
                        isSinglePaymentMethod = true,
                    )
                }
            }
        }
    }

    private fun addListener() {
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
        viewModel.trackPaymentCancelled()
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