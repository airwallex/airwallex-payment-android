package com.airwallex.android.view

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
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
import com.airwallex.android.databinding.DialogAddCardBinding
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.view.composables.PaymentElement
import com.airwallex.android.view.composables.PaymentElementConfiguration
import com.airwallex.android.view.util.AnalyticsConstants.CARD_PAYMENT_VIEW
import com.airwallex.android.view.util.AnalyticsConstants.EVENT_PAYMENT_CANCELLED
import com.airwallex.android.view.util.AnalyticsConstants.SUPPORTED_SCHEMES
import com.airwallex.risk.AirwallexRisk
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("InflateParams")
class AirwallexAddPaymentDialog @JvmOverloads constructor(
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
        get() = CARD_PAYMENT_VIEW

    override val additionalInfo: Map<String, Any>
        get() = mapOf(SUPPORTED_SCHEMES to supportedCardSchemes.map { it.name })

    private val airwallex: Airwallex by lazy {
        Airwallex(activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        AirwallexRisk.log(AirwallexRisk.Events.TRANSACTION_INITIATED)
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

        // Set background with rounded corners (12dp on top)
        val cornerRadius = context.resources.displayMetrics.density * 12
        viewBinding.root.background = GradientDrawable().apply {
            setColor(AirwallexColor.backgroundPrimary.toArgb())
            cornerRadii = floatArrayOf(
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius,
                0f, 0f,
                0f, 0f
            )
        }
        viewBinding.cardLabel.setTextColor(AirwallexColor.textPrimary.toArgb())
        viewBinding.closeIcon.setColorFilter(AirwallexColor.iconPrimary.toArgb())
        viewBinding.composeView.apply {
            setContent {
                AddPaymentDialogContent()
            }
        }
    }

    @Composable
    private fun AddPaymentDialogContent() {
        AirwallexTheme {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                PaymentElementContent()
            }
        }
    }

    @Composable
    private fun PaymentElementContent() {
        var paymentState by remember { mutableStateOf<PaymentElement?>(null) }

        LaunchedEffect(Unit) {
            setLoadingProgress(true)
            PaymentElement.create(
                session = session,
                airwallex = airwallex,
                configuration = PaymentElementConfiguration.Card(
                    supportedCardBrands = supportedCardSchemes
                ),
                launchType = AnalyticsLogger.LaunchType.COMPONENT,
                onLoadingStateChanged = { isLoading ->
                    setLoadingProgress(isLoading)
                },
                onPaymentResult = { status ->
                    when (status) {
                        is AirwallexPaymentStatus.Success -> {
                            dismissWithPaymentResult(
                                paymentIntentId = status.paymentIntentId,
                                paymentConsentId = status.consentId
                            )
                        }

                        is AirwallexPaymentStatus.Failure -> {
                            dismissWithPaymentResult(exception = status.exception)
                        }

                        else -> Unit
                    }
                },
                onError = { _ ->
                    // shouldn't be any error
                }
            ).fold(
                onSuccess = { state ->
                    paymentState = state
                    setLoadingProgress(false)
                },
                onFailure = { error ->
                    setLoadingProgress(false)
                    dismiss()
                }
            )
        }

        paymentState?.Content()
    }

    private fun addListener() {
        viewBinding.closeIcon.setOnClickListener {
            cancelPayment()
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
        AnalyticsLogger.logAction(actionName = EVENT_PAYMENT_CANCELLED)
        paymentResultListener.onCompleted(AirwallexPaymentStatus.Cancel)
        dismiss()
    }

    private fun setLoadingProgress(loading: Boolean) {
        viewBinding.frLoading.visibility = if (loading) View.VISIBLE else View.GONE
    }
}