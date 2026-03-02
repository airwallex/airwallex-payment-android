package com.airwallex.android.view

import android.content.Intent
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.databinding.ActivityAddCardBinding
import com.airwallex.android.ui.checkout.AirwallexCheckoutBaseActivity
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.ui.extension.getExtraArgs
import com.airwallex.android.view.composables.PaymentElementConfiguration
import com.airwallex.android.view.composables.PaymentElementManager
import com.airwallex.android.view.util.AnalyticsConstants.CARD_PAYMENT_VIEW
import com.airwallex.android.view.util.AnalyticsConstants.SUPPORTED_SCHEMES
import com.airwallex.risk.AirwallexRisk

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
        get() = CARD_PAYMENT_VIEW

    override val additionalInfo: Map<String, Any>
        get() = mapOf(SUPPORTED_SCHEMES to args.supportedCardSchemes.map { it.name })

    override val session: AirwallexSession by lazy {
        args.session
    }

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    override val paymentLaunchSubtype: String = "component"
    override val paymentMethodName: String = PaymentMethodType.CARD.value

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    override fun initView() {
        super.initView()
        AirwallexRisk.log(event = "show_create_card", screen = "page_create_card")
        viewBinding.composeView.apply {
            setContent {
                AirwallexTheme {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                    ) {
                        StandardText(
                            text = stringResource(id = R.string.airwallex_new_card),
                            color = AirwallexColor.TextPrimary,
                            typography = AirwallexTypography.Title200,
                            textAlign = TextAlign.Left,
                        )
                        PaymentElementContent()
                    }
                }
            }
        }
    }

    @Composable
    private fun PaymentElementContent() {
        var paymentState by remember { mutableStateOf<PaymentElementManager?>(null) }

        LaunchedEffect(Unit) {
            setLoadingProgress(loading = true, cancelable = false)
            PaymentElementManager.create(
                session = session,
                airwallex = airwallex,
                configuration = PaymentElementConfiguration.Card(
                    supportedCardBrands = args.supportedCardSchemes
                ),
                onLoadingStateChanged = { isLoading ->
                    setLoadingProgress(loading = isLoading, cancelable = false)
                },
                onPaymentResult = { status ->
                    when (status) {
                        is AirwallexPaymentStatus.Success -> {
                            finishWithPaymentIntent(
                                paymentIntentId = status.paymentIntentId,
                                consentId = status.consentId,
                            )
                        }

                        is AirwallexPaymentStatus.Failure -> {
                            finishWithPaymentIntent(exception = status.exception)
                        }

                        else -> Unit
                    }
                },
                onError = { _ ->
                    // Shouldn't be any error since data is pre-fetched
                }
            ).fold(
                onSuccess = { state ->
                    paymentState = state
                    setLoadingProgress(loading = false, cancelable = false)
                },
                onFailure = { error ->
                    setLoadingProgress(loading = false, cancelable = false)
                    alert(message = error.message ?: "Failed to load payment methods")
                }
            )
        }
        paymentState?.Content()
    }

    override fun onBackButtonPressed() {
        super.onBackButtonPressed()
        AirwallexLogger.info("AddPaymentMethodActivity onBackButtonPressed")
        setResult(
            RESULT_CANCELED,
            Intent().putExtras(
                AddPaymentMethodActivityLaunch.CancellationResult(
                    isSinglePaymentMethod = args.isSinglePaymentMethod,
                ).toBundle()
            )
        )
        finish()
    }

    private fun finishWithPaymentIntent(
        paymentIntentId: String? = null,
        consentId: String? = null,
        exception: AirwallexException? = null
    ) {
        setLoadingProgress(false)
        AirwallexLogger.info("AddPaymentMethodActivity finishWithPaymentIntent")
        setResult(
            RESULT_OK,
            Intent().putExtras(
                AddPaymentMethodActivityLaunch.Result(
                    paymentIntentId = paymentIntentId,
                    consentId = consentId,
                    exception = exception,
                ).toBundle()
            )
        )
        finish()
    }
}