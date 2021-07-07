package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.*
import com.airwallex.android.databinding.ActivityPaymentMethodsBinding
import com.airwallex.android.model.*
import com.airwallex.android.view.AirwallexCheckoutViewModel.*
import kotlin.collections.ArrayList

/**
 * Allow the customer to select one of the payment methods, or add a new one via [AddPaymentMethodActivity].
 *
 */
internal class PaymentMethodsActivity : AirwallexCheckoutBaseActivity() {

    private val viewBinding: ActivityPaymentMethodsBinding by lazy {
        viewStub.layoutResource = R.layout.activity_payment_methods
        val root = viewStub.inflate() as ViewGroup
        ActivityPaymentMethodsBinding.bind(root)
    }

    private lateinit var paymentMethodsAdapter: PaymentMethodsAdapter

    private val args: PaymentMethodsActivityLaunch.Args by lazy {
        PaymentMethodsActivityLaunch.Args.getExtra(intent)
    }

    override val session: AirwallexSession by lazy {
        args.session
    }

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

    private var availableThirdPaymentTypes: List<PaymentMethodType>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchPaymentMethods()
    }

    private fun initView(availableThirdPaymentTypes: List<PaymentMethodType>) {
        val shouldShowCard = availableThirdPaymentTypes.contains(PaymentMethodType.CARD)
        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        paymentMethodsAdapter = PaymentMethodsAdapter(
            availableThirdPaymentTypes = availableThirdPaymentTypes.filter { it != PaymentMethodType.CARD },
            shouldShowCard = shouldShowCard
        )

        paymentMethodsAdapter.listener = object : PaymentMethodsAdapter.Listener {
            override fun onPaymentConsentClick(paymentConsent: PaymentConsent) {
                handleProcessPaymentMethod(
                    paymentConsent = paymentConsent
                )
            }
        }

        viewBinding.addPaymentMethod.container.visibility =
            if (shouldShowCard) View.VISIBLE else View.GONE
        viewBinding.addPaymentMethod.container.setOnClickListener {
            startAddPaymentMethod()
        }

        viewBinding.rvPaymentMethods.apply {
            layoutManager = viewManager
            adapter = paymentMethodsAdapter

            addItemDecoration(
                PaymentMethodsDividerItemDecoration(
                    this@PaymentMethodsActivity,
                    R.drawable.airwallex_line_divider,
                    availableThirdPaymentTypeSize = availableThirdPaymentTypes.filter { it != PaymentMethodType.CARD }.size
                )
            )
        }

        val deletePaymentMethodDialogFactory = DeletePaymentMethodDialogFactory(
            this,
            paymentMethodsAdapter,
        ) { paymentConsent ->
            setLoadingProgress(loading = true, cancelable = false)
            viewModel.deletePaymentConsent(paymentConsent).observe(
                this,
                {
                    when (it) {
                        is PaymentMethodsViewModel.PaymentConsentResult.Success -> {
                            setLoadingProgress(false)
                            paymentMethodsAdapter.deletePaymentConsent(paymentConsent)
                        }
                        is PaymentMethodsViewModel.PaymentConsentResult.Error -> {
                            setLoadingProgress(false)
                            alert(message = it.exception.message ?: it.exception.toString())
                        }
                    }
                }
            )
        }

        object :
            PaymentMethodSwipeCallback(this@PaymentMethodsActivity, viewBinding.rvPaymentMethods) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder?,
                underlayButtons: ArrayList<UnderlayButton>
            ) {
                underlayButtons.add(
                    UnderlayButton(
                        text = resources.getString(R.string.airwallex_delete_payment_method_positive),
                        textSize = resources.getDimensionPixelSize(R.dimen.swipe_size),
                        color = ContextCompat.getColor(
                            baseContext,
                            R.color.airwallex_swipe_bg_color
                        ),
                        clickListener = object : UnderlayButtonClickListener {
                            override fun onClick(position: Int) {
                                deletePaymentMethodDialogFactory
                                    .create(
                                        paymentMethodsAdapter.getPaymentConsentAtPosition(
                                            position
                                        )
                                    )
                                    .show()
                            }
                        }
                    )
                )
            }
        }
    }

    private fun fetchPaymentMethods() {
        setLoadingProgress(loading = true, cancelable = false)
        viewModel.fetchPaymentMethodTypes().observe(
            this@PaymentMethodsActivity,
            {
                when (it) {
                    is PaymentMethodsViewModel.PaymentMethodTypeResult.Success -> {
                        setLoadingProgress(loading = false)
                        // Complete load available payment method type
                        this.availableThirdPaymentTypes = it.availableThirdPaymentTypes
                        initView(it.availableThirdPaymentTypes)
                        filterPaymentConsents()
                    }
                    is PaymentMethodsViewModel.PaymentMethodTypeResult.Error -> {
                        setLoadingProgress(loading = false)
                        alert(message = it.exception.message ?: it.exception.toString())
                    }
                }
            }
        )
    }

    private fun filterPaymentConsents() {
        if (availableThirdPaymentTypes?.contains(PaymentMethodType.CARD) == false) {
            return
        }

        val paymentIntent = viewModel.paymentIntent
        val customerPaymentConsents = paymentIntent?.customerPaymentConsents ?: return
        val customerPaymentMethods = paymentIntent.customerPaymentMethods ?: return

        if (session is AirwallexPaymentSession) {
            val paymentConsents = customerPaymentConsents.filter {
                it.nextTriggeredBy == PaymentConsent.NextTriggeredBy.CUSTOMER && it.status == PaymentConsent.PaymentConsentStatus.VERIFIED && it.paymentMethod?.type == PaymentMethodType.CARD
            }
            paymentConsents.forEach { paymentConsent ->
                paymentConsent.paymentMethod =
                    customerPaymentMethods.find { paymentMethod -> paymentMethod.id == paymentConsent.paymentMethod?.id }
            }
            paymentConsents.let {
                paymentMethodsAdapter.setPaymentConsents(it)
            }
        }
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    private fun startAddPaymentMethod() {
        AddPaymentMethodActivityLaunch(this@PaymentMethodsActivity)
            .startForResult(
                AddPaymentMethodActivityLaunch.Args.Builder()
                    .setAirwallexSession(session)
                    .build()
            )
    }

    private fun handleProcessPaymentMethod(
        paymentConsent: PaymentConsent,
        cvc: String? = null
    ) = if (args.includeCheckoutFlow) {
        val paymentMethod = requireNotNull(paymentConsent.paymentMethod)
        val observer = Observer<PaymentResult> {
            when (it) {
                is PaymentResult.Success -> {
                    finishWithPaymentIntent(paymentIntent = it.paymentIntent)
                }
                is PaymentResult.Error -> {
                    finishWithPaymentIntent(exception = it.exception)
                }
                is PaymentResult.WeChatPay -> {
                    finishWithPaymentIntent(weChat = it.weChat)
                }
                is PaymentResult.Redirect -> {
                    finishWithPaymentIntent(redirectUrl = it.redirectUrl)
                }
            }
        }

        when (paymentMethod.type) {
            PaymentMethodType.CARD -> {
                setLoadingProgress(false)

                // Start `PaymentCheckoutActivity` to confirm `PaymentIntent`
                PaymentCheckoutActivityLaunch(this@PaymentMethodsActivity)
                    .startForResult(
                        PaymentCheckoutActivityLaunch.Args.Builder()
                            .setAirwallexSession(session)
                            .setPaymentMethod(paymentMethod)
                            .setPaymentConsentId(paymentConsent.id)
                            .setCvc(cvc)
                            .build()
                    )
            }
            else -> {
                val paymentMethodType = requireNotNull(paymentConsent.paymentMethod?.type)
                val requiredFields = paymentMethodType.requiredFields

                if (requiredFields.isEmpty()) {
                    startCheckout(
                        paymentMethod = paymentMethod,
                        paymentConsentId = paymentConsent.id,
                        cvc = null,
                        currency = session.currency,
                        observer = observer
                    )
                } else {
                    fun showPaymentInfoDialog(
                        requiredFields: List<PaymentMethodRequiredField>,
                        bank: Bank? = null
                    ) {
                        val paymentInfoDialog = PaymentInfoBottomSheetDialog.newInstance(
                            paymentMethodType,
                            getString(R.string.airwallex_pay_with, paymentMethodType.displayName),
                            requiredFields
                        )
                        paymentInfoDialog.onCompleted = { name, email, phone ->
                            startCheckout(
                                paymentMethod = paymentMethod,
                                paymentConsentId = paymentConsent.id,
                                cvc = null,
                                currency = session.currency,
                                bank = bank,
                                name = name,
                                email = email,
                                phone = phone,
                                observer = observer
                            )
                        }
                        paymentInfoDialog.show(supportFragmentManager, paymentMethodType.value)
                    }

                    if (listOf(
                            PaymentMethodType.BANK_TRANSFER,
                            PaymentMethodType.ONLINE_BANKING
                        ).any { it == paymentMethodType }
                    ) {
                        val bankDialog = PaymentBankBottomSheetDialog.newInstance(
                            getString(R.string.airwallex_select_your_bank),
                            session.currency
                        )
                        bankDialog.onCompleted = { bank ->
                            val fields = if (session.currency == "IDR") {
                                requiredFields.filter { it != PaymentMethodRequiredField.SHOPPER_PHONE }
                            } else {
                                requiredFields
                            }
                            showPaymentInfoDialog(fields, bank)
                        }
                        bankDialog.show(supportFragmentManager, paymentMethodType.value)
                    } else {
                        showPaymentInfoDialog(requiredFields)
                    }
                }
            }
        }
    } else {
        // Return the `PaymentMethod` 'cvc' to merchant
        finishWithPaymentMethod(
            paymentMethod = paymentConsent.paymentMethod,
            paymentConsentId = paymentConsent.id,
            cvc = cvc
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            AddPaymentMethodActivityLaunch.REQUEST_CODE -> {
                val result = AddPaymentMethodActivityLaunch.Result.fromIntent(data)
                result?.let {
                    if (session is AirwallexPaymentSession) {
                        finishWithPaymentIntent(
                            paymentIntent = result.paymentIntent,
                            exception = result.exception
                        )
                    } else {
                        viewBinding.rvPaymentMethods.requestLayout()
                        handleProcessPaymentMethod(
                            PaymentConsent(paymentMethod = it.paymentMethod),
                            it.cvc
                        )
                    }
                }
            }

            PaymentCheckoutActivityLaunch.REQUEST_CODE -> {
                val result = PaymentCheckoutActivityLaunch.Result.fromIntent(data)
                result?.let {
                    finishWithPaymentIntent(
                        paymentIntent = it.paymentIntent,
                        exception = it.exception
                    )
                }
            }
        }
    }

    private fun finishWithPaymentMethod(
        paymentMethod: PaymentMethod? = null,
        paymentConsentId: String? = null,
        cvc: String? = null,
        exception: Exception? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                PaymentMethodsActivityLaunch.Result(
                    paymentMethod = paymentMethod,
                    paymentConsentId = paymentConsentId,
                    cvc = cvc,
                    includeCheckoutFlow = args.includeCheckoutFlow,
                    exception = exception
                ).toBundle()
            )
        )
        finish()
    }

    private fun finishWithPaymentIntent(
        paymentIntent: PaymentIntent? = null,
        weChat: WeChat? = null,
        redirectUrl: String? = null,
        exception: Exception? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                PaymentMethodsActivityLaunch.Result(
                    paymentIntent = paymentIntent,
                    exception = exception,
                    weChat = weChat,
                    redirectUrl = redirectUrl,
                    includeCheckoutFlow = args.includeCheckoutFlow
                ).toBundle()
            )
        )
        finish()
    }
}
