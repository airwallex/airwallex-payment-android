package com.airwallex.android.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.AirwallexSupportedCard
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.isExpressCheckout
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.databinding.DialogAddCardBinding
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.view.composables.AwxPaymentElement
import com.airwallex.android.view.composables.AwxPaymentElementConfiguration
import com.airwallex.android.view.util.AnalyticsConstants.CARD_PAYMENT_VIEW
import com.airwallex.android.view.util.AnalyticsConstants.EVENT_PAYMENT_CANCELLED
import com.airwallex.android.view.util.AnalyticsConstants.SUPPORTED_SCHEMES
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
        AnalyticsLogger.logAction(
            actionName = "payment_launched",
            additionalInfo = mapOf(
                "subtype" to "component",
                "paymentMethod" to PaymentMethodType.CARD.value,
                "expressCheckout" to session.isExpressCheckout
            )
        )
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
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        AwxPaymentElement(
                            session = session,
                            airwallex = airwallex,
                            configuration = AwxPaymentElementConfiguration.Card(
                                cardSchemes = supportedCardSchemes
                            ),
                            operationListener = object : PaymentOperationListener {
                                override fun onLoadingStateChanged(isLoading: Boolean) {
                                    setLoadingProgress(isLoading)
                                }

                                override fun onPaymentResult(status: AirwallexPaymentStatus) {
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
                                }

                                override fun onError(exception: Throwable) {
                                    // Show error to user
                                    android.widget.Toast.makeText(
                                        activity,
                                        exception.message ?: "An error occurred",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                        )
                    }
                }
            }
        }
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