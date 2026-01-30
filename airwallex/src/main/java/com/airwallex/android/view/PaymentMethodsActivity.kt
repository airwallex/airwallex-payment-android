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
import com.airwallex.android.core.model.CardScheme
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

    private lateinit var addPaymentMethodViewModel: AddPaymentMethodViewModel

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    override val paymentLaunchSubtype: String = "dropin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLoadingProgress(loading = true, cancelable = false)

        airwallex.updateActivity(this)
        viewModel.fetchPaymentMethodsAndConsents()
    }

    override fun onBackButtonPressed() {
        super.onBackButtonPressed()
        AirwallexLogger.info("PaymentMethodsActivity onBackButtonPressed")
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun addObserver() {
        viewModel.paymentMethodResult.observe(this) { result ->
            setLoadingProgress(loading = false)
            when (result) {
                is PaymentMethodsViewModel.PaymentMethodResult.Show -> {
                    initView(result.methods.first, result.methods.second)
                }
            }
        }
        viewModel.paymentFlowStatus.observe(this) { flowStatus ->
            setLoadingProgress(loading = false)
            when (flowStatus) {
                is PaymentMethodsViewModel.PaymentFlowStatus.PaymentStatus -> {
                    handlePaymentStatus(flowStatus.status)
                }

                is PaymentMethodsViewModel.PaymentFlowStatus.ErrorAlert -> {
                    alert(message = flowStatus.message)
                }
            }
        }
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    private fun initView(
        availablePaymentMethodTypes: List<AvailablePaymentMethodType>,
        availablePaymentConsents: List<PaymentConsent>,
    ) {
        // Initialize AddPaymentMethodViewModel only if not already initialized
        if (!::addPaymentMethodViewModel.isInitialized) {
            val supportedCardSchemes = availablePaymentMethodTypes.firstOrNull { paymentMethodType ->
                paymentMethodType.name == PaymentMethodType.CARD.value
            }?.cardSchemes ?: emptyList()

            addPaymentMethodViewModel = ViewModelProvider(
                this,
                AddPaymentMethodViewModel.Factory(
                    application = application,
                    airwallex = airwallex,
                    session = session,
                    supportedCardSchemes = supportedCardSchemes
                ),
            )[AddPaymentMethodViewModel::class.java]
            addPaymentMethodViewModel.updateActivity(this)

        }

        AirwallexRisk.log(event = "show_payment_method_list", screen = "page_payment_method_list")
        val allowedPaymentMethods = session.googlePayOptions?.let { googlePayOptions ->
            availablePaymentMethodTypes.firstOrNull { paymentMethodType ->
                paymentMethodType.name == PaymentMethodType.GOOGLEPAY.value
            }?.let { paymentMethodType ->
                GooglePayUtil.retrieveAllowedPaymentMethods(
                    googlePayOptions,
                    paymentMethodType.cardSchemes,
                )
            }
        }

        viewBinding.composeView.apply {
            setContent {
                AirwallexTheme {
                    PaymentScreen(
                        session = session,
                        airwallex = airwallex,
                        layoutType = args.layoutType,
                        paymentMethodsViewModel = viewModel,
                        addPaymentMethodViewModel = addPaymentMethodViewModel,
                        allowedPaymentMethods = allowedPaymentMethods,
                        availablePaymentMethodTypes = availablePaymentMethodTypes,
                        availablePaymentConsents = availablePaymentConsents,
                        onCheckoutWithoutCvc = ::onCheckoutWithoutCvc,
                        onCheckoutWithCvc = ::onCheckoutWithCvc,
                        onDirectPay = ::onDirectPay,
                        onPayWithFields = ::onPayWithSchema,
                        onLoading = { isLoading ->
                            setLoadingProgress(loading = isLoading)
                        },
                        onOperationStart = { operation ->
                            when (operation) {
                                is PaymentOperation.AddCard -> {
                                    setLoadingProgress(loading = true, cancelable = false)
                                    AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to PaymentMethodType.CARD.value))
                                    onAddCard()
                                }
                                else -> {} // Other operations will be handled later
                            }
                        },
                        onOperationDone = { result ->
                            setLoadingProgress(loading = false)
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

                                is PaymentOperationResult.DeleteCard -> {
                                    setLoadingProgress(false)
                                    result.result.fold(
                                        onSuccess = { consent ->
                                            // nothing to do
                                        },
                                        onFailure = { exception ->
                                            alert(message = exception.message ?: exception.toString())
                                        }
                                    )
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

    private fun onCheckoutWithoutCvc(paymentConsent: PaymentConsent) {
        setLoadingProgress(true, cancelable = false)
        viewModel.trackPaymentSelection(paymentConsent.paymentMethod?.type)
        viewModel.confirmPaymentIntent(paymentConsent)
    }

    private fun onCheckoutWithCvc(paymentConsent: PaymentConsent, cvc: String) {
        setLoadingProgress(true, cancelable = false)
        viewModel.trackPaymentSelection(paymentConsent.paymentMethod?.type)
        viewModel.checkoutWithCvc(paymentConsent, cvc)
    }

    private fun onAddCard() {
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.trackCardPaymentSelection()
        AirwallexRisk.log(event = "click_payment_button", screen = "page_create_card")
    }

    private fun onDirectPay(type: AvailablePaymentMethodType) {
        setLoadingProgress(loading = true, cancelable = false)
        AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to type.name))
        viewModel.checkoutWithSchema(type)
    }

    private fun onPayWithSchema(
        paymentMethod: PaymentMethod,
        info: PaymentMethodTypeInfo,
        fieldMap: Map<String, String>,
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        AnalyticsLogger.logAction("tap_pay_button", mapOf("payment_method" to info.name.orEmpty()))
        viewModel.checkoutWithSchema(
            paymentMethod = paymentMethod,
            additionalInfo = fieldMap,
            typeInfo = info,
        )
    }
}
