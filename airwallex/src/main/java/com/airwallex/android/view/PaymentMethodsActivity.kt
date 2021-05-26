package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.*
import com.airwallex.android.ClientSecretRepository
import com.airwallex.android.databinding.ActivityPaymentMethodsBinding
import com.airwallex.android.model.*
import java.util.concurrent.atomic.AtomicInteger

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

    private val session: AirwallexSession by lazy {
        args.session
    }

    private val customerId: String by lazy {
        requireNotNull(session.customerId)
    }

    private val paymentIntent: PaymentIntent? by lazy {
        when (session) {
            is AirwallexPaymentSession -> {
                (session as AirwallexPaymentSession).paymentIntent
            }
            is AirwallexRecurringWithIntentSession -> {
                (session as AirwallexRecurringWithIntentSession).paymentIntent
            }
            else -> {
                null
            }
        }
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
        this.availableThirdPaymentTypes = availableThirdPaymentTypes
        val shouldShowCard = availableThirdPaymentTypes.contains(PaymentMethodType.CARD)
        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        paymentMethodsAdapter = PaymentMethodsAdapter(
            availableThirdPaymentTypes = availableThirdPaymentTypes.filter { it != PaymentMethodType.CARD },
            shouldShowCard = shouldShowCard
        )

        paymentMethodsAdapter.listener = object : PaymentMethodsAdapter.Listener {
            override fun onPaymentConsentClick(paymentConsent: PaymentConsent) {
                handleProcessPaymentMethod(paymentConsent)
            }
        }

        viewBinding.addPaymentMethod.container.visibility = if (shouldShowCard) View.VISIBLE else View.GONE
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
        ) {
            setLoadingProgress(loading = true, cancelable = false)
            ClientSecretRepository.getInstance().retrieveClientSecret(
                customerId,
                object : ClientSecretRepository.ClientSecretRetrieveListener {
                    override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                        airwallex.disablePaymentConsent(
                            DisablePaymentConsentParams(
                                clientSecret = clientSecret.value,
                                paymentConsentId = requireNotNull(it.id),
                            ),
                            object : Airwallex.PaymentListener<PaymentConsent> {

                                override fun onFailed(exception: Exception) {
                                    setLoadingProgress(false)
                                    alert(message = exception.message ?: exception.toString())
                                }

                                override fun onSuccess(response: PaymentConsent) {
                                    setLoadingProgress(false)
                                    paymentMethodsAdapter.deletePaymentConsent(it)
                                }
                            }
                        )
                    }

                    override fun onClientSecretError(errorMessage: String) {
                        setLoadingProgress(false)
                        alert(message = errorMessage)
                    }
                }
            )
        }

        object : PaymentMethodSwipeCallback(this@PaymentMethodsActivity, viewBinding.rvPaymentMethods) {
            override fun instantiateUnderlayButton(viewHolder: RecyclerView.ViewHolder?, underlayButtons: ArrayList<UnderlayButton>) {
                underlayButtons.add(
                    UnderlayButton(
                        text = resources.getString(R.string.delete_payment_method_positive),
                        textSize = resources.getDimensionPixelSize(R.dimen.swipe_size),
                        color = ContextCompat.getColor(baseContext, R.color.airwallex_swipe_bg_color),
                        clickListener = object : UnderlayButtonClickListener {
                            override fun onClick(position: Int) {
                                deletePaymentMethodDialogFactory
                                    .create(paymentMethodsAdapter.getPaymentConsentAtPosition(position))
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
        ClientSecretRepository.getInstance().retrieveClientSecret(
            customerId,
            object : ClientSecretRepository.ClientSecretRetrieveListener {
                override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                    if (availableThirdPaymentTypes == null) {
                        retrieveAvailablePaymentMethods(
                            mutableListOf(),
                            AtomicInteger(0),
                            clientSecret = clientSecret,
                            onFailed = { exception ->
                                setLoadingProgress(loading = false)
                                alert(message = exception.message ?: exception.toString())
                            },
                            onCompleted = {
                                setLoadingProgress(loading = false)
                                // Complete load available payment method type
                                initView(it)
                                retrievePaymentConsents()
                            }
                        )
                    } else {
                        setLoadingProgress(loading = false)
                        retrievePaymentConsents()
                    }
                }

                override fun onClientSecretError(errorMessage: String) {
                    setLoadingProgress(loading = false)
                    alert(message = errorMessage)
                }
            }
        )
    }

    private fun retrieveAvailablePaymentMethods(
        availablePaymentMethodList: MutableList<AvailablePaymentMethod>,
        availablePaymentMethodPageNum: AtomicInteger,
        clientSecret: ClientSecret,
        onFailed: (exception: Exception) -> Unit,
        onCompleted: (availableThirdPaymentTypes: List<PaymentMethodType>) -> Unit
    ) {
        airwallex.retrieveAvailablePaymentMethods(
            params = RetrieveAvailablePaymentMethodParams.Builder(
                clientSecret = requireNotNull(clientSecret.value),
                pageNum = availablePaymentMethodPageNum.get()
            )
                .setActive(true)
                .setTransactionCurrency(session.currency)
                .build(),
            listener = object : Airwallex.PaymentListener<AvailablePaymentMethodResponse> {
                override fun onFailed(exception: Exception) {
                    onFailed.invoke(exception)
                }

                override fun onSuccess(response: AvailablePaymentMethodResponse) {
                    availablePaymentMethodPageNum.incrementAndGet()
                    availablePaymentMethodList.addAll(response.items ?: emptyList())
                    if (response.hasMore) {
                        retrieveAvailablePaymentMethods(availablePaymentMethodList, availablePaymentMethodPageNum, clientSecret, onFailed, onCompleted)
                    } else {
                        when (args.session) {
                            is AirwallexRecurringSession, is AirwallexRecurringWithIntentSession -> {
                                onCompleted.invoke(availablePaymentMethodList.filter { it.transactionMode == AvailablePaymentMethod.TransactionMode.RECURRING }.mapNotNull { it.name }.distinct())
                            }
                            is AirwallexPaymentSession -> {
                                onCompleted.invoke(availablePaymentMethodList.filter { it.transactionMode == AvailablePaymentMethod.TransactionMode.ONE_OFF }.mapNotNull { it.name }.distinct())
                            }
                            else -> Unit
                        }
                    }
                }
            }
        )
    }

    private fun retrievePaymentConsents() {
        if (availableThirdPaymentTypes?.contains(PaymentMethodType.CARD) == false) {
            return
        }

        val customerPaymentConsents = paymentIntent?.customerPaymentConsents ?: return
        val customerPaymentMethods = paymentIntent?.customerPaymentMethods ?: return

        if (session is AirwallexPaymentSession) {
            val paymentConsents = customerPaymentConsents.filter {
                it.nextTriggeredBy == PaymentConsent.NextTriggeredBy.CUSTOMER && it.status == PaymentConsent.PaymentConsentStatus.VERIFIED && it.paymentMethod?.type == PaymentMethodType.CARD
            }
            paymentConsents.forEach { paymentConsent ->
                paymentConsent.paymentMethod = customerPaymentMethods.find { paymentMethod -> paymentMethod.id == paymentConsent.paymentMethod?.id }
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
        ClientSecretRepository.getInstance().retrieveClientSecret(
            customerId,
            object : ClientSecretRepository.ClientSecretRetrieveListener {
                override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                    AddPaymentMethodActivityLaunch(this@PaymentMethodsActivity)
                        .startForResult(
                            AddPaymentMethodActivityLaunch.Args.Builder()
                                .setAirwallexSession(session)
                                .build()
                        )
                }

                override fun onClientSecretError(errorMessage: String) {
                    setLoadingProgress(loading = false)
                    alert(message = errorMessage)
                }
            }
        )
    }

    private fun handleProcessPaymentMethod(paymentConsent: PaymentConsent, cvc: String? = null) {
        if (args.includeCheckoutFlow) {
            val paymentMethod = requireNotNull(paymentConsent.paymentMethod)
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
                    startCheckout(
                        session = session,
                        paymentMethod = paymentMethod,
                        paymentConsentId = paymentConsent.id,
                        cvc = null,
                        listener = object : Airwallex.PaymentResultListener<PaymentIntent> {
                            override fun onSuccess(response: PaymentIntent) {
                                finishWithPaymentIntent(paymentIntent = response)
                            }

                            override fun onFailed(exception: Exception) {
                                finishWithPaymentIntent(exception = exception)
                            }

                            override fun onNextActionWithWeChatPay(weChat: WeChat) {
                                finishWithPaymentIntent(weChat = weChat)
                            }

                            override fun onNextActionWithAlipayUrl(url: String) {
                                finishWithPaymentIntent(redirectUrl = url)
                            }
                        }
                    )
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
                    viewBinding.rvPaymentMethods.requestLayout()
                    handleProcessPaymentMethod(
                        PaymentConsent(
                            paymentMethod = it.paymentMethod
                        ),
                        it.cvc
                    )
                }
            }

            PaymentCheckoutActivityLaunch.REQUEST_CODE -> {
                val result = PaymentCheckoutActivityLaunch.Result.fromIntent(data)
                result?.let {
                    finishWithPaymentIntent(paymentIntent = it.paymentIntent, exception = it.exception)
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
