package com.airwallex.android.view

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.databinding.ActivityPaymentMethodsBinding
import com.airwallex.android.ui.checkout.AirwallexCheckoutBaseActivity
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.ui.extension.getExtraArgs
import com.airwallex.android.view.composables.PaymentScreen
import com.airwallex.android.view.composables.card.PaymentOperation
import com.airwallex.android.view.composables.card.PaymentOperationResult
import com.airwallex.android.view.util.GooglePayUtil
import com.airwallex.risk.AirwallexRisk

@Suppress("LongMethod")
class PaymentMethodsActivity : AirwallexCheckoutBaseActivity(), TrackablePage {

    private val viewBinding: ActivityPaymentMethodsBinding by lazy {
        viewStub.layoutResource = R.layout.activity_payment_methods
        val root = viewStub.inflate() as ViewGroup
        ActivityPaymentMethodsBinding.bind(root)
    }

    private val args: PaymentMethodsActivityLaunch.Args by lazy {
        intent.getExtraArgs()
    }

    override val session: AirwallexSession by lazy {
        args.session
    }

    override val pageName: String
        get() = viewModel.pageName

    private val viewModel: PaymentMethodsViewModel by lazy {
        ViewModelProvider(
            this,
            PaymentMethodsViewModel.Factory(
                application, airwallex, session
            ),
        )[PaymentMethodsViewModel::class.java]
    }

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    override val paymentLaunchSubtype: String = "dropin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLoadingProgress(loading = true, cancelable = false)

        viewModel.updateActivity(this)
        initView(listOf(), listOf())
    }

    override fun onBackButtonPressed() {
        super.onBackButtonPressed()
        AirwallexLogger.info("PaymentMethodsActivity onBackButtonPressed")
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun addObserver() {
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    private fun initView(
        availablePaymentMethodTypes: List<AvailablePaymentMethodType>,
        availablePaymentConsents: List<PaymentConsent>,
    ) {
        // Update the latest values
        AirwallexRisk.log(event = "show_payment_method_list", screen = "page_payment_method_list")
        viewBinding.composeView.apply {
            setContent {
                AirwallexTheme {
                    PaymentScreen(
                        session = session,
                        airwallex = airwallex,
                        layoutType = args.layoutType,
                        availablePaymentMethodTypes = availablePaymentMethodTypes,
                        availablePaymentConsents = availablePaymentConsents,
                        onOperationStart = { operation ->
                            when (operation) {
                                is PaymentOperation.AddCard -> {
                                    onAddCard()
                                }

                                is PaymentOperation.CheckoutWithCvc,
                                is PaymentOperation.CheckoutWithoutCvc -> {
                                    setLoadingProgress(loading = true, cancelable = false)
                                    val paymentMethodType = when (operation) {
                                        is PaymentOperation.CheckoutWithCvc -> operation.consent.paymentMethod?.type
                                        is PaymentOperation.CheckoutWithoutCvc -> operation.consent.paymentMethod?.type
                                        else -> null
                                    }
                                    viewModel.trackPaymentSelection(paymentMethodType)
                                }

                                is PaymentOperation.DirectPay,
                                is PaymentOperation.PayWithFields,
                                is PaymentOperation.DeleteCard,
                                PaymentOperation.CheckoutWithGooglePay,
                                PaymentOperation.FetchPaymentMethods,
                                PaymentOperation.LoadSchemaFields -> {
                                    setLoadingProgress(loading = true, cancelable = false)
                                }
                            }
                        },
                        onOperationDone = { result ->
                            setLoadingProgress(loading = false)
                            when (result) {
                                is PaymentOperationResult.AddCard -> handlePaymentStatus(result.status)

                                is PaymentOperationResult.DeleteCard -> {
                                    setLoadingProgress(false)
                                    result.result.fold(
                                        onSuccess = { _ ->
                                            // nothing to do
                                        },
                                        onFailure = { exception ->
                                            alert(
                                                message = exception.message ?: exception.toString()
                                            )
                                        }
                                    )
                                }

                                is PaymentOperationResult.CheckoutWithCvc -> handlePaymentStatus(
                                    result.status
                                )

                                is PaymentOperationResult.CheckoutWithoutCvc -> handlePaymentStatus(
                                    result.status
                                )

                                is PaymentOperationResult.DirectPay -> handlePaymentStatus(result.status)
                                is PaymentOperationResult.PayWithFields -> handlePaymentStatus(
                                    result.status
                                )

                                is PaymentOperationResult.Error -> {
                                    alert(message = result.message)
                                }

                                is PaymentOperationResult.FetchPaymentMethods -> {
                                    // Data is already available in the composable's ViewModel state
                                }

                                is PaymentOperationResult.CheckoutWithGooglePay -> {
                                    handlePaymentStatus(result.status)
                                }

                                is PaymentOperationResult.LoadSchemaFields -> {
                                    // Just stop loading, nothing else needed
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    private fun handlePaymentStatus(status: AirwallexPaymentStatus) {
        when (status) {
            is AirwallexPaymentStatus.Success -> {
                finishWithPaymentIntent(
                    paymentIntentId = status.paymentIntentId,
                    isRedirecting = false,
                )
            }

            is AirwallexPaymentStatus.Failure -> {
                finishWithPaymentIntent(exception = status.exception)
            }

            is AirwallexPaymentStatus.InProgress -> {
                finishWithPaymentIntent(
                    paymentIntentId = status.paymentIntentId,
                    isRedirecting = true,
                )
            }

            is AirwallexPaymentStatus.Cancel -> {
                setLoadingProgress(false)
            }
        }
    }

    private fun finishWithPaymentIntent(
        paymentIntentId: String? = null,
        isRedirecting: Boolean = false,
        exception: AirwallexException? = null,
        consentId: String? = null,
    ) {
        setLoadingProgress(false)
        AirwallexLogger.info("PaymentMethodsActivity finishWithPaymentIntent")
        setResult(
            RESULT_OK,
            Intent().putExtras(
                PaymentMethodsActivityLaunch.Result(
                    paymentIntentId = paymentIntentId,
                    paymentConsentId = consentId,
                    isRedirecting = isRedirecting,
                    exception = exception
                ).toBundle()
            ),
        )
        finish()
    }

    private fun onAddCard() {
        setLoadingProgress(loading = true, cancelable = false)
        AnalyticsLogger.logAction(
            "tap_pay_button",
            mapOf("payment_method" to PaymentMethodType.CARD.value)
        )
        viewModel.trackCardPaymentSelection()
        AirwallexRisk.log(event = "click_payment_button", screen = "page_create_card")
    }
}
