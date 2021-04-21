package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.*
import com.airwallex.android.ClientSecretRepository
import com.airwallex.android.model.*
import kotlinx.android.synthetic.main.activity_payment_methods.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Allow the customer to select one of the payment methods, or add a new one via [AddPaymentMethodActivity].
 *
 */
internal class PaymentMethodsActivity : AirwallexCheckoutBaseActivity() {

    private var pageNum: AtomicInteger = AtomicInteger(0)

    private lateinit var paymentMethodsAdapter: PaymentMethodsAdapter

    private val args: PaymentMethodsActivityLaunch.Args by lazy {
        PaymentMethodsActivityLaunch.Args.getExtra(intent)
    }

    override val paymentIntent: PaymentIntent by lazy {
        args.paymentIntent
    }

    override val cvc: String?
        get() = null

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    private var availableThirdPaymentTypes: List<AvaliablePaymentMethodType>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchPaymentMethods()
    }

    private fun initView(availableThirdPaymentTypes: List<AvaliablePaymentMethodType>) {
        this.availableThirdPaymentTypes = availableThirdPaymentTypes
        val shouldShowCard = availableThirdPaymentTypes.contains(AvaliablePaymentMethodType.CARD)
        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        paymentMethodsAdapter = PaymentMethodsAdapter(
            availableThirdPaymentTypes = availableThirdPaymentTypes.filter { it != AvaliablePaymentMethodType.CARD },
            shouldShowCard = shouldShowCard
        )

        paymentMethodsAdapter.listener = object : PaymentMethodsAdapter.Listener {
            override fun onPaymentMethodClick(paymentMethod: PaymentMethod) {
                processPaymentMethod(paymentMethod)
            }
        }

        addPaymentMethod.visibility = if (shouldShowCard) View.VISIBLE else View.GONE
        addPaymentMethod.setOnClickListener {
            startAddPaymentMethod()
        }

        rvPaymentMethods.apply {
            layoutManager = viewManager
            adapter = paymentMethodsAdapter

            addItemDecoration(
                PaymentMethodsDividerItemDecoration(
                    this@PaymentMethodsActivity,
                    R.drawable.airwallex_line_divider,
                    availableThirdPaymentTypeSize = availableThirdPaymentTypes.filter { it != AvaliablePaymentMethodType.CARD }.size
                )
            )
        }

        paymentMethodsAdapter.onLoadMoreCallback = {
            fetchPaymentMethods()
        }
        paymentMethodsAdapter.addOnScrollListener(rvPaymentMethods)

        val deletePaymentMethodDialogFactory = DeletePaymentMethodDialogFactory(
            this,
            paymentMethodsAdapter,
        ) {
            setLoadingProgress(loading = true, cancelable = false)
            ClientSecretRepository.getInstance().retrieveClientSecret(
                requireNotNull(paymentIntent.customerId),
                object : ClientSecretRepository.ClientSecretRetrieveListener {
                    override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                        airwallex.disablePaymentMethod(
                            DisablePaymentMethodParams(
                                clientSecret = clientSecret.value,
                                customerId = requireNotNull(paymentIntent.customerId),
                                paymentMethodId = requireNotNull(it.id),
                            ),
                            object : Airwallex.PaymentListener<PaymentMethod> {
                                override fun onSuccess(response: PaymentMethod) {
                                    setLoadingProgress(false)
                                    paymentMethodsAdapter.deletePaymentMethod(it)
                                }

                                override fun onFailed(exception: Exception) {
                                    setLoadingProgress(false)
                                    alert(message = exception.message ?: exception.toString())
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

        object : PaymentMethodSwipeCallback(this@PaymentMethodsActivity, rvPaymentMethods) {
            override fun instantiateUnderlayButton(viewHolder: RecyclerView.ViewHolder?, underlayButtons: ArrayList<UnderlayButton>) {
                underlayButtons.add(
                    UnderlayButton(
                        text = resources.getString(R.string.delete_payment_method_positive),
                        textSize = resources.getDimensionPixelSize(R.dimen.swipe_size),
                        color = ContextCompat.getColor(baseContext, R.color.airwallex_swipe_bg_color),
                        clickListener = object : UnderlayButtonClickListener {
                            override fun onClick(position: Int) {
                                deletePaymentMethodDialogFactory
                                    .create(paymentMethodsAdapter.getPaymentMethodAtPosition(position))
                                    .show()
                            }
                        }
                    )
                )
            }
        }
    }

    override val layoutResource: Int
        get() = R.layout.activity_payment_methods

    private fun fetchPaymentMethods() {
        setLoadingProgress(loading = true, cancelable = false)
        ClientSecretRepository.getInstance().retrieveClientSecret(
            requireNotNull(paymentIntent.customerId),
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
                                retrievePaymentMethods(clientSecret)
                            }
                        )
                    } else {
                        setLoadingProgress(loading = false)
                        retrievePaymentMethods(clientSecret)
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
        onCompleted: (availableThirdPaymentTypes: List<AvaliablePaymentMethodType>) -> Unit
    ) {
        airwallex.retrieveAvailablePaymentMethods(
            params = RetrieveAvailablePaymentMethodParams.Builder(
                clientSecret = requireNotNull(clientSecret.value),
                pageNum = availablePaymentMethodPageNum.get()
            )
                .setActive(true)
                .setTransactionCurrency(paymentIntent.currency)
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
                        val availableThirdPaymentTypes = if (args.checkoutMode == AirwallexCheckoutMode.RECURRING) {
                            availablePaymentMethodList.filter { it.transactionMode == AvailablePaymentMethod.TransactionMode.RECURRING }.mapNotNull { AvaliablePaymentMethodType.fromValue(it.name) }.distinct()
                        } else {
                            availablePaymentMethodList.filter { it.transactionMode == AvailablePaymentMethod.TransactionMode.ONE_OFF }.mapNotNull { AvaliablePaymentMethodType.fromValue(it.name) }.distinct()
                        }
                        onCompleted.invoke(availableThirdPaymentTypes)
                    }
                }
            }
        )
    }

    private fun retrievePaymentMethods(clientSecret: ClientSecret) {
        if (availableThirdPaymentTypes?.contains(AvaliablePaymentMethodType.CARD) == false) {
            return
        }
        paymentMethodsAdapter.startLoadingMore()
        airwallex.retrievePaymentMethods(
            params = RetrievePaymentMethodParams.Builder(
                customerId = requireNotNull(paymentIntent.customerId),
                clientSecret = requireNotNull(clientSecret.value),
                type = AvaliablePaymentMethodType.CARD,
                pageNum = pageNum.get()
            )
                .build(),
            listener = object : Airwallex.PaymentListener<PaymentMethodResponse> {
                override fun onSuccess(response: PaymentMethodResponse) {
                    paymentMethodsAdapter.endLoadingMore()
                    paymentMethodsAdapter.setPaymentMethods(response.items, response.hasMore)
                    paymentNoCards.visibility = if (paymentMethodsAdapter.isEmpty()) View.VISIBLE else View.GONE
                    pageNum.incrementAndGet()
                }

                override fun onFailed(exception: Exception) {
                    alert(message = exception.message ?: exception.toString())
                    paymentMethodsAdapter.endLoadingMore()
                    paymentMethodsAdapter.setPaymentMethods(arrayListOf(), false)
                }
            }
        )
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    private fun startAddPaymentMethod() {
        AddPaymentMethodActivityLaunch(this@PaymentMethodsActivity)
            .startForResult(
                AddPaymentMethodActivityLaunch.Args
                    .Builder()
                    .setShipping(paymentIntent.order?.shipping)
                    .setCustomerId(requireNotNull(paymentIntent.customerId))
                    .setClientSecret(requireNotNull(paymentIntent.clientSecret))
                    .build()
            )
    }

    private fun processPaymentMethod(paymentMethod: PaymentMethod, cvc: String? = null) {
        if (args.checkoutMode == AirwallexCheckoutMode.RECURRING) {
            setLoadingProgress(loading = true, cancelable = false)
            createAndVerifyPaymentConsent(
                paymentMethod = paymentMethod,
                if (args.nextTriggerBy == AirwallexNextTriggerBy.MERCHANT) PaymentConsent.NextTriggeredBy.MERCHANT else PaymentConsent.NextTriggeredBy.CUSTOMER,
                listener = object : Airwallex.PaymentListener<PaymentConsent> {
                    override fun onFailed(exception: Exception) {
                        finishWithPaymentMethod(exception = exception)
                    }

                    override fun onSuccess(response: PaymentConsent) {
                        handleProcessPaymentMethod(paymentMethod, response, cvc)
                    }
                }
            )
        } else {
            handleProcessPaymentMethod(paymentMethod, null, cvc)
        }
    }

    private fun handleProcessPaymentMethod(paymentMethod: PaymentMethod, paymentConsent: PaymentConsent?, cvc: String? = null) {
        if (args.includeCheckoutFlow) {
            when (paymentMethod.type) {
                PaymentMethodType.CARD -> {
                    setLoadingProgress(false)
                    // Start `PaymentCheckoutActivity` to confirm `PaymentIntent`
                    PaymentCheckoutActivityLaunch(this@PaymentMethodsActivity)
                        .startForResult(
                            PaymentCheckoutActivityLaunch.Args.Builder()
                                .setPaymentIntent(paymentIntent)
                                .setPaymentMethod(paymentMethod)
                                .setPaymentConsent(paymentConsent)
                                .setCvc(cvc)
                                .build()
                        )
                }
                else -> {
                    confirmPaymentIntent(
                        paymentMethod = paymentMethod,
                        paymentConsent = paymentConsent,
                        listener = object : Airwallex.PaymentListener<PaymentIntent> {
                            override fun onSuccess(response: PaymentIntent) {
                                finishWithPaymentIntent(paymentIntent = response)
                            }

                            override fun onFailed(exception: Exception) {
                                finishWithPaymentIntent(exception = exception)
                            }
                        }
                    )
                }
            }
        } else {
            // Return the `PaymentMethod` & 'PaymentConsent' & 'cvc' to merchant
            finishWithPaymentMethod(
                paymentMethod = paymentMethod,
                paymentConsent = paymentConsent,
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
                    paymentMethodsAdapter.addNewPaymentMethod(it.paymentMethod)
                    paymentNoCards.visibility =
                        if (paymentMethodsAdapter.isEmpty()) View.VISIBLE else View.GONE
                    rvPaymentMethods.requestLayout()
                    processPaymentMethod(it.paymentMethod, it.cvc)
                }
            }
            PaymentCheckoutActivityLaunch.REQUEST_CODE -> {
                val result = PaymentCheckoutActivityLaunch.Result.fromIntent(data)
                result?.let {
                    finishWithPaymentIntent(it.paymentIntent, it.exception)
                }
            }
        }
    }

    private fun finishWithPaymentMethod(
        paymentMethod: PaymentMethod? = null,
        paymentConsent: PaymentConsent? = null,
        cvc: String? = null,
        exception: Exception? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                PaymentMethodsActivityLaunch.Result(
                    paymentMethod = paymentMethod,
                    paymentConsent = paymentConsent,
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
        exception: Exception? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                PaymentMethodsActivityLaunch.Result(
                    paymentIntent = paymentIntent,
                    exception = exception,
                    includeCheckoutFlow = args.includeCheckoutFlow
                ).toBundle()
            )
        )
        finish()
    }
}
