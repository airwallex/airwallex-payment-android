package com.airwallex.android.view

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Bank
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.DynamicSchemaField
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.databinding.ActivityPaymentMethodsBinding
import com.airwallex.android.ui.checkout.AirwallexCheckoutBaseActivity
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.ui.extension.getExtraArgs
import com.airwallex.android.view.PaymentMethodsViewModel.Companion.COUNTRY_CODE
import com.airwallex.android.view.composables.PaymentScreen
import com.airwallex.android.view.util.GooglePayUtil
import com.airwallex.risk.AirwallexRisk

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
                application,
                airwallex,
                session
            )
        )[PaymentMethodsViewModel::class.java]
    }

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.fetchPaymentMethodsAndConsents()
    }

    override fun onBackButtonPressed() {
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

                is PaymentMethodsViewModel.PaymentMethodResult.Skip -> {
                    startAddPaymentMethod(result.schemes, true)
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

                is PaymentMethodsViewModel.PaymentFlowStatus.SchemaFieldsDialog -> {
                    showSchemaFieldsDialog(flowStatus.typeInfo, flowStatus.paymentMethod)
                }

                is PaymentMethodsViewModel.PaymentFlowStatus.BankDialog -> {
                    showPaymentBankDialog(
                        flowStatus.paymentMethod,
                        flowStatus.typeInfo,
                        flowStatus.bankField,
                        flowStatus.banks
                    )
                }
            }
        }
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    private val paymentMethodsAdapterListener = object : PaymentMethodsAdapter.Listener {

        override fun onPaymentConsentClick(paymentConsent: PaymentConsent) {
            setLoadingProgress(true)
            viewModel.trackPaymentSelection(paymentConsent.paymentMethod?.type)
            viewModel.confirmPaymentIntent(paymentConsent)
        }

        override fun onPaymentMethodClick(paymentMethodType: AvailablePaymentMethodType) {
            AirwallexLogger.info("PaymentMethodsActivity onPaymentMethodClick: type = ${paymentMethodType.name}")
            setLoadingProgress(true)
            viewModel.trackPaymentSelection(paymentMethodType.name)
            viewModel.startCheckout(paymentMethodType)
        }

        override fun onAddCardClick(supportedCardSchemes: List<CardScheme>) {
            viewModel.trackCardPaymentSelection()
            startAddPaymentMethod(supportedCardSchemes, isSinglePaymentMethod = false)
        }
    }

    private fun initView(
        availablePaymentMethodTypes: List<AvailablePaymentMethodType>,
        availablePaymentConsents: List<PaymentConsent>,
    ) {
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
        val addPaymentMethodViewModel = ViewModelProvider(
            owner = this,
            factory = AddPaymentMethodViewModel.Factory(
                application = application,
                airwallex = airwallex,
                session = session,
                supportedCardSchemes = availablePaymentMethodTypes.firstOrNull { paymentMethodType ->
                    paymentMethodType.name == PaymentMethodType.CARD.value
                }?.cardSchemes ?: emptyList(),
            )
        )[AddPaymentMethodViewModel::class.java]

        addPaymentMethodViewModel.airwallexPaymentStatus.observe(this) { result ->
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

        viewBinding.composeView.apply {
            setContent {
                AirwallexTheme {
                    PaymentScreen(
                        paymentMethodsViewModel = viewModel,
                        addPaymentMethodViewModel = addPaymentMethodViewModel,
                        allowedPaymentMethods = allowedPaymentMethods,
                        availablePaymentMethodTypes = availablePaymentMethodTypes,
                        availablePaymentConsents = availablePaymentConsents,
                        onAddCard = ::onAddCard,
                        onDeleteCard = { consent ->
                            onDeleteCard(consent) {
                                addPaymentMethodViewModel.deleteCard(consent)
                            }
                        },
                        onPaymentConsentClicked = ::onPaymentConsentClicked,
                        onCheckoutWithCvc = ::onCheckoutWithCvc,
                    )
                }
            }
        }
    }

    private fun startAddPaymentMethod(
        cardSchemes: List<CardScheme>,
        isSinglePaymentMethod: Boolean
    ) {
        AddPaymentMethodActivityLaunch(this@PaymentMethodsActivity)
            .launchForResult(
                AddPaymentMethodActivityLaunch.Args.Builder()
                    .setAirwallexSession(session)
                    .setSupportedCardSchemes(cardSchemes)
                    .setSinglePaymentMethod(isSinglePaymentMethod)
                    .build()
            ) { _, result ->
                handleAddPaymentMethodActivityResult(result.resultCode, result.data)
            }
    }

    private fun showPaymentBankDialog(
        paymentMethod: PaymentMethod,
        typeInfo: PaymentMethodTypeInfo,
        bankField: DynamicSchemaField? = null,
        banks: List<Bank>
    ) {
        val bankDialog = PaymentBankBottomSheetDialog.newInstance(
            paymentMethod,
            getString(R.string.airwallex_select_your_bank),
            banks
        )
        bankDialog.onCompleted = { bank ->
            AnalyticsLogger.logAction("select_bank", mapOf("bankName" to bank.name))
            showSchemaFieldsDialog(typeInfo, paymentMethod, bankField, bank.name)
        }
        bankDialog.show(supportFragmentManager, typeInfo.name)
    }

    private fun showSchemaFieldsDialog(
        info: PaymentMethodTypeInfo,
        paymentMethod: PaymentMethod,
        bankField: DynamicSchemaField? = null,
        bankName: String? = null,
    ) {
        val paymentInfoDialog = PaymentInfoBottomSheetDialog.newInstance(paymentMethod, info)
        paymentInfoDialog.onCompleted = { fieldMap ->
            setLoadingProgress(loading = true)
            if (bankField != null && bankName != null) {
                fieldMap[bankField.name] = bankName
            }
            if (session is AirwallexPaymentSession) {
                fieldMap[COUNTRY_CODE] = (session as AirwallexPaymentSession).countryCode
            }
            setLoadingProgress(loading = true, cancelable = false)
            viewModel.startCheckout(
                paymentMethod = paymentMethod,
                additionalInfo = fieldMap,
                typeInfo = info
            )
        }
        paymentInfoDialog.show(
            supportFragmentManager,
            info.name
        )
    }

    private fun handleAddPaymentMethodActivityResult(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val result = AddPaymentMethodActivityLaunch.Result.fromIntent(data)
            result?.let {
                if (it.exception == null) {
                    viewModel.trackCardPaymentSuccess()
                }
                finishWithPaymentIntent(
                    paymentIntentId = result.paymentIntentId,
                    exception = result.exception,
                    consentId = it.consentId
                )
            }
        } else if (resultCode == RESULT_CANCELED) {
            val result = AddPaymentMethodActivityLaunch.CancellationResult.fromIntent(data)
            AirwallexLogger.info("PaymentMethodsActivity onActivityResult: result_canceled")
            result?.let {
                if (it.isSinglePaymentMethod) {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            }
        }
    }

    private fun handlePaymentStatus(status: AirwallexPaymentStatus) {
        when (status) {
            is AirwallexPaymentStatus.Success -> {
                finishWithPaymentIntent(
                    paymentIntentId = status.paymentIntentId,
                    isRedirecting = false
                )
            }

            is AirwallexPaymentStatus.Failure -> {
                finishWithPaymentIntent(exception = status.exception)
            }

            is AirwallexPaymentStatus.InProgress -> {
                finishWithPaymentIntent(
                    paymentIntentId = status.paymentIntentId,
                    isRedirecting = true
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
            )
        )
        finish()
    }

    private fun onDeleteCard(paymentConsent: PaymentConsent, onDeleteCompleted: (PaymentConsent) -> Unit) {
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.deletePaymentConsent(paymentConsent).observe(this) { result ->
            result.fold(
                onSuccess = { consent ->
                    setLoadingProgress(false)
                    onDeleteCompleted(consent)
                },
                onFailure = {
                    setLoadingProgress(false)
                    alert(message = it.message ?: it.toString())
                },
            )
        }
    }

    private fun onPaymentConsentClicked(paymentConsent: PaymentConsent) {
        setLoadingProgress(true)
        viewModel.trackPaymentSelection(paymentConsent.paymentMethod?.type)
        viewModel.confirmPaymentIntent(paymentConsent)
    }

    private fun onCheckoutWithCvc(paymentConsent: PaymentConsent, cvc: String) {
        setLoadingProgress(true)
        viewModel.trackPaymentSelection(paymentConsent.paymentMethod?.type)
        viewModel.checkoutWithCvc(paymentConsent, cvc)
    }

    private fun onAddCard() {
        AnalyticsLogger.logAction("tap_pay_button")
        AirwallexRisk.log(event = "click_payment_button", screen = "page_create_card")
        setLoadingProgress(loading = true, cancelable = false)
    }

    companion object {
        private const val TAG = "PaymentMethodsActivity"
    }
}
