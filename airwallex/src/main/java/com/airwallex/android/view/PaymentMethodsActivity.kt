package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.databinding.ActivityPaymentMethodsBinding
import com.airwallex.android.R
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.log.Logger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.DynamicSchemaField
import com.airwallex.android.core.model.DynamicSchemaFieldType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.PaymentMethodTypeInfo
import com.airwallex.android.view.PaymentMethodsViewModel.Companion.COUNTRY_CODE
import kotlinx.coroutines.launch

class PaymentMethodsActivity : AirwallexCheckoutBaseActivity() {

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

    private var availablePaymentMethodTypes: List<AvailablePaymentMethodType>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchPaymentMethods()
    }

    private fun initView(availablePaymentMethodTypes: List<AvailablePaymentMethodType>) {
        val viewManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        paymentMethodsAdapter = PaymentMethodsAdapter(
            availablePaymentMethodTypes = availablePaymentMethodTypes,
            listener = object : PaymentMethodsAdapter.Listener {
                override fun onPaymentConsentClick(
                    paymentConsent: PaymentConsent,
                    paymentMethodType: AvailablePaymentMethodType?
                ) {
                    handleProcessPaymentMethod(
                        paymentConsent = paymentConsent,
                        paymentMethodType = paymentMethodType
                    )
                }

                override fun onAddCardClick() {
                    startAddPaymentMethod()
                }
            }
        )

        viewBinding.rvPaymentMethods.apply {
            layoutManager = viewManager
            adapter = paymentMethodsAdapter

            addItemDecoration(
                PaymentMethodsDividerItemDecoration(
                    this@PaymentMethodsActivity,
                    R.drawable.airwallex_line_divider
                )
            )
        }

        val deletePaymentMethodDialogFactory = DeletePaymentMethodDialogFactory(
            this,
            paymentMethodsAdapter,
        ) { paymentConsent ->
            setLoadingProgress(loading = true, cancelable = false)
            viewModel.deletePaymentConsent(paymentConsent).observe(
                this
            ) { result ->
                result.fold(
                    onSuccess = {
                        setLoadingProgress(false)
                        paymentMethodsAdapter.deletePaymentConsent(it)
                    },
                    onFailure = {
                        setLoadingProgress(false)
                        alert(message = it.message ?: it.toString())
                    }
                )
            }
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
                            R.color.airwallex_color_red
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
        lifecycleScope.launch {
            setLoadingProgress(loading = true, cancelable = false)
            viewModel.fetchAvailablePaymentMethodTypes().observe(
                this@PaymentMethodsActivity
            ) { result ->
                result.fold(
                    onSuccess = {
                        setLoadingProgress(loading = false)
                        // Complete load available payment method type
                        this@PaymentMethodsActivity.availablePaymentMethodTypes = it
                        initView(it)
                        filterPaymentConsents()
                    },
                    onFailure = {
                        setLoadingProgress(loading = false)
                        alert(message = it.message ?: it.toString())
                    }
                )
            }
        }
    }

    // To be extracted to view model so that it can be tested
    private fun filterPaymentConsents() {
        if (availablePaymentMethodTypes?.find { it.name == PaymentMethodType.CARD.value } == null) {
            return
        }

        val paymentIntent = viewModel.paymentIntent
        val customerPaymentConsents = paymentIntent?.customerPaymentConsents ?: return
        val customerPaymentMethods = paymentIntent.customerPaymentMethods ?: return

        if (session is AirwallexPaymentSession) {
            val paymentConsents = customerPaymentConsents.filter {
                it.nextTriggeredBy == PaymentConsent.NextTriggeredBy.CUSTOMER &&
                    it.status == PaymentConsent.PaymentConsentStatus.VERIFIED &&
                    it.paymentMethod?.type == PaymentMethodType.CARD.value
            }
            paymentConsents.forEach { paymentConsent ->
                paymentConsent.paymentMethod =
                    customerPaymentMethods.find { paymentMethod ->
                        paymentMethod.id == paymentConsent.paymentMethod?.id
                    }
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
        paymentMethodType: AvailablePaymentMethodType?,
        cvc: String? = null
    ) {
        val paymentMethod = requireNotNull(paymentConsent.paymentMethod)
        val observer = Observer<AirwallexPaymentStatus> { result ->

            when (result) {
                is AirwallexPaymentStatus.Success -> {
                    finishWithPaymentIntent(
                        paymentIntentId = result.paymentIntentId,
                        isRedirecting = false
                    )
                }
                is AirwallexPaymentStatus.Failure -> {
                    finishWithPaymentIntent(exception = result.exception)
                }
                is AirwallexPaymentStatus.InProgress -> {
                    finishWithPaymentIntent(
                        paymentIntentId = result.paymentIntentId,
                        isRedirecting = true
                    )
                }
                else -> Unit
            }
        }

        when (paymentMethod.type) {
            PaymentMethodType.CARD.value -> {
                setLoadingProgress(false)
                // Start `PaymentCheckoutActivity` to confirm `PaymentIntent`
                if (paymentConsent.requiresCvc) {
                    PaymentCheckoutActivityLaunch(this@PaymentMethodsActivity)
                        .startForResult(
                            PaymentCheckoutActivityLaunch.Args.Builder()
                                .setAirwallexSession(session)
                                .setPaymentMethod(paymentMethod)
                                .setPaymentConsentId(paymentConsent.id)
                                .setCvc(cvc)
                                .build()
                        )
                } else {
                    startCheckout(
                        paymentMethod = paymentMethod,
                        paymentConsentId = paymentConsent.id,
                        observer = observer
                    )
                }
            }
            PaymentMethodType.GOOGLEPAY.value -> {
                setLoadingProgress(false)
                startCheckout(
                    paymentMethod = paymentMethod,
                    paymentConsentId = paymentConsent.id,
                    observer = observer
                )
            }
            else -> {
                if (paymentMethodType?.resources?.hasSchema == true && session is AirwallexPaymentSession) {
                    // Have required schema fields
                    // 1. Retrieve all required schema fields of the payment method
                    // 2. If the bank is needed, need to retrieve the bank list.
                    // 3. If the bank is not needed, then show the schema fields dialog.
                    Logger.debug(TAG, "Get more payment Info fields on one-off flow.")
                    paymentMethod.type?.let { type ->
                        retrievePaymentMethodTypeInfo(type) { result ->
                            result.fold(
                                onSuccess = { info ->
                                    val fields = viewModel.filterRequiredFields(info)
                                    if (fields == null || fields.isEmpty()) {
                                        // If all fields are hidden, start checkout directly
                                        startCheckout(
                                            paymentMethod = paymentMethod,
                                            paymentConsentId = paymentConsent.id,
                                            observer = observer
                                        )
                                        return@fold
                                    }

                                    val bankField =
                                        fields.find { field -> field.type == DynamicSchemaFieldType.BANKS }
                                    if (bankField != null) {
                                        retrieveBanks(type) { result ->
                                            result.fold(
                                                onSuccess = {
                                                    setLoadingProgress(loading = false)
                                                    val bankDialog =
                                                        PaymentBankBottomSheetDialog.newInstance(
                                                            getString(R.string.airwallex_select_your_bank),
                                                            it.items ?: mutableListOf()
                                                        )
                                                    bankDialog.onCompleted = { bank ->
                                                        showSchemaFieldsDialog(
                                                            info = info,
                                                            paymentMethod = paymentMethod,
                                                            bankField = bankField,
                                                            bankName = bank.name,
                                                            observer = observer
                                                        )
                                                    }
                                                    bankDialog.show(
                                                        supportFragmentManager,
                                                        info.name
                                                    )
                                                },
                                                onFailure = {
                                                    setLoadingProgress(loading = false)
                                                    alert(message = it.message ?: it.toString())
                                                }
                                            )
                                        }
                                    } else {
                                        setLoadingProgress(loading = false)
                                        showSchemaFieldsDialog(
                                            info = info,
                                            paymentMethod = paymentMethod,
                                            observer = observer
                                        )
                                    }
                                },
                                onFailure = {
                                    setLoadingProgress(loading = false)
                                    alert(message = it.message ?: it.toString())
                                }
                            )
                        }
                    }
                } else {
                    startCheckout(
                        paymentMethod = paymentMethod,
                        paymentConsentId = paymentConsent.id,
                        observer = observer
                    )
                }
            }
        }
    }

    private fun showSchemaFieldsDialog(
        info: PaymentMethodTypeInfo,
        paymentMethod: PaymentMethod,
        bankField: DynamicSchemaField? = null,
        bankName: String? = null,
        observer: Observer<AirwallexPaymentStatus>
    ) {
        val paymentInfoDialog = PaymentInfoBottomSheetDialog.newInstance(info)
        paymentInfoDialog.onCompleted = { fieldMap ->
            setLoadingProgress(loading = true)
            if (bankField != null && bankName != null) {
                fieldMap[bankField.name] = bankName
            }
            if (session is AirwallexPaymentSession) {
                fieldMap[COUNTRY_CODE] = (session as AirwallexPaymentSession).countryCode
            }
            startCheckout(
                paymentMethod = paymentMethod,
                additionalInfo = fieldMap,
                flow = viewModel.fetchPaymentFlow(info),
                observer = observer
            )
        }
        paymentInfoDialog.show(
            supportFragmentManager,
            info.name
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        airwallex.handlePaymentData(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            AddPaymentMethodActivityLaunch.REQUEST_CODE -> {
                val result = AddPaymentMethodActivityLaunch.Result.fromIntent(data)
                result?.let {
                    finishWithPaymentIntent(
                        paymentIntentId = result.paymentIntentId,
                        exception = result.exception
                    )
                }
            }

            PaymentCheckoutActivityLaunch.REQUEST_CODE -> {
                val result = PaymentCheckoutActivityLaunch.Result.fromIntent(data)
                result?.let {
                    finishWithPaymentIntent(
                        paymentIntentId = it.paymentIntentId,
                        exception = it.exception
                    )
                }
            }
        }
    }

    private fun finishWithPaymentIntent(
        paymentIntentId: String? = null,
        isRedirecting: Boolean = false,
        exception: AirwallexException? = null
    ) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                PaymentMethodsActivityLaunch.Result(
                    paymentIntentId = paymentIntentId,
                    isRedirecting = isRedirecting,
                    exception = exception
                ).toBundle()
            )
        )
        finish()
    }

    companion object {
        private const val TAG = "PaymentMethodsActivity"
    }
}
