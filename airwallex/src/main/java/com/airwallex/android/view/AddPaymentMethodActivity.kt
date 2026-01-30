package com.airwallex.android.view

import android.content.Intent
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.databinding.ActivityAddCardBinding
import com.airwallex.android.ui.checkout.AirwallexCheckoutBaseActivity
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.ui.composables.AirwallexTypography
import com.airwallex.android.ui.composables.StandardText
import com.airwallex.android.ui.extension.getExtraArgs
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.composables.card.PaymentOperation
import com.airwallex.android.view.composables.card.PaymentOperationResult
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
        get() = viewModel.pageName

    override val additionalInfo: Map<String, Any>
        get() = viewModel.additionalInfo

    override val session: AirwallexSession by lazy {
        args.session
    }

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    override val paymentLaunchSubtype: String = "component"
    override val paymentMethodName: String = PaymentMethodType.CARD.value

    private val viewModel: AddPaymentMethodViewModel by lazy {
        ViewModelProvider(
            this,
            AddPaymentMethodViewModel.Factory(
                application, airwallex, session, args.supportedCardSchemes
            )
        )[AddPaymentMethodViewModel::class.java]
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    override fun initView() {
        super.initView()
        airwallex.updateActivity(this)
        AirwallexRisk.log(event = "show_create_card", screen = "page_create_card")

        viewBinding.composeView.apply {
            setContent {
                AirwallexTheme {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        StandardText(
                            text = stringResource(id = R.string.airwallex_new_card),
                            color = AirwallexColor.TextPrimary,
                            typography = AirwallexTypography.Title200,
                            textAlign = TextAlign.Left,
                            modifier = Modifier.padding(horizontal = 24.dp),
                        )
                        CardSection(
                            session = session,
                            airwallex = airwallex,
                            addPaymentMethodViewModel = viewModel,
                            cardSchemes = args.supportedCardSchemes,
                            onCheckoutWithoutCvc = {},
                            onCheckoutWithCvc = { _, _ -> },
                            isSinglePaymentMethod = args.isSinglePaymentMethod,
                            onOperationStart = { operation ->
                                when (operation) {
                                    is PaymentOperation.AddCard -> {
                                        setLoadingProgress(loading = true, cancelable = false)
                                        onAddCard()
                                    }
                                    else -> {} // Other operations will be handled later
                                }
                            },
                            onOperationDone = { result ->
                                when (result) {
                                    is PaymentOperationResult.AddCard -> {
                                        when (result.status) {
                                            is AirwallexPaymentStatus.Success -> {
                                                finishWithPaymentIntent(
                                                    paymentIntentId = result.status.paymentIntentId,
                                                    consentId = result.status.consentId,
                                                )
                                            }
                                            is AirwallexPaymentStatus.Failure -> {
                                                finishWithPaymentIntent(exception = result.status.exception)
                                            }
                                            else -> Unit
                                        }
                                    }

                                    else -> {}
                                }
                            },
                            needFetchConsentsAndSchemes = false
                        )
                    }
                }
            }
        }
    }

//    override fun addListener() {
//        super.addListener()
//        viewModel.airwallexPaymentStatus.observe(this) { result ->
//            when (result) {
//                is AirwallexPaymentStatus.Success -> {
//                    finishWithPaymentIntent(
//                        paymentIntentId = result.paymentIntentId, consentId = result.consentId
//                    )
//                }
//                is AirwallexPaymentStatus.Failure -> {
//                    finishWithPaymentIntent(exception = result.exception)
//                }
//                else -> Unit
//            }
//        }
//    }

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

    private fun onAddCard() {
        setLoadingProgress(loading = true, cancelable = false)
        AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to PaymentMethodType.CARD.value))
        AirwallexRisk.log(event = "click_payment_button", screen = "page_create_card")
    }
}
