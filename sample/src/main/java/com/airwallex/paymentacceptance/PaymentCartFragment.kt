package com.airwallex.paymentacceptance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.*
import com.airwallex.android.exception.RedirectException
import com.airwallex.android.model.*
import com.airwallex.android.model.Address
import com.airwallex.android.model.parser.PaymentIntentParser
import com.airwallex.paymentacceptance.databinding.CartItemBinding
import com.airwallex.paymentacceptance.databinding.FragmentCartBinding
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.math.BigDecimal
import java.util.*

class PaymentCartFragment : Fragment() {

    private val airwallex by lazy {
        Airwallex(this)
    }

    private val viewBinding: FragmentCartBinding by lazy {
        FragmentCartBinding.inflate(layoutInflater)
    }

    private val clientSecretProvider by lazy {
        ExampleClientSecretProvider()
    }

    private val viewModel: PaymentCartViewModel by lazy {
        ViewModelProvider(
            this,
            PaymentCartViewModel.Factory(
                requireActivity().application,
                airwallex
            )
        )[PaymentCartViewModel::class.java]
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        activity?.runOnUiThread {
            if (throwable is HttpException) {
                showCreatePaymentIntentError(
                    throwable.response()?.errorBody()?.string() ?: throwable.localizedMessage
                )
            } else {
                showCreatePaymentIntentError(throwable.localizedMessage)
            }
        }
    }

    private val api: Api
        get() {
            if (TextUtils.isEmpty(AirwallexPlugins.environment.baseUrl())) {
                throw IllegalArgumentException("Base url should not be null or empty")
            }
            return ApiFactory(AirwallexPlugins.environment.baseUrl()).buildRetrofit()
                .create(Api::class.java)
        }

    private var shipping: Shipping = Shipping.Builder()
        .setFirstName("Verify")
        .setLastName("Doe")
        .setPhone("13800000000")
        .setAddress(
            Address.Builder()
                .setCountryCode("CN")
                .setState("Shanghai")
                .setCity("Shanghai")
                .setStreet("Pudong District")
                .setPostcode("100000")
                .build()
        )
        .build()

    private val products = mutableListOf(
        PhysicalProduct.Builder()
            .setCode("123")
            .setName("AirPods Pro")
            .setDesc("Buy AirPods Pro, per month with trade-in")
            .setSku("piece")
            .setType("Free engraving")
            .setUnitPrice(399.00)
            .setUrl("www.aircross.com")
            .setQuantity(1)
            .build(),
        PhysicalProduct.Builder()
            .setCode("123")
            .setName("HomePod")
            .setDesc("Buy HomePod, per month with trade-in")
            .setSku("piece")
            .setType("White")
            .setUnitPrice(469.00)
            .setUrl("www.aircross.com")
            .setQuantity(1)
            .build()
    )

    private val checkoutMode: AirwallexCheckoutMode
        get() {
            return when (Settings.checkoutMode) {
                SampleApplication.instance.resources.getStringArray(R.array.array_checkout_mode)[0] -> AirwallexCheckoutMode.PAYMENT
                SampleApplication.instance.resources.getStringArray(R.array.array_checkout_mode)[1] -> AirwallexCheckoutMode.RECURRING
                SampleApplication.instance.resources.getStringArray(R.array.array_checkout_mode)[2] -> AirwallexCheckoutMode.RECURRING_WITH_INTENT
                else -> throw Exception("Unsupported CheckoutMode: ${Settings.checkoutMode}")
            }
        }

    private val nextTriggerBy: PaymentConsent.NextTriggeredBy
        get() {
            return when (Settings.nextTriggerBy) {
                SampleApplication.instance.resources.getStringArray(R.array.array_next_trigger_by)[0] -> PaymentConsent.NextTriggeredBy.MERCHANT
                SampleApplication.instance.resources.getStringArray(R.array.array_next_trigger_by)[1] -> PaymentConsent.NextTriggeredBy.CUSTOMER
                else -> throw Exception("Unsupported NextTriggerBy: ${Settings.nextTriggerBy}")
            }
        }

    private val returnUrl: String
        get() {
            return "airwallexcheckout://${context?.packageName}"
//            return when (Settings.sdkEnv) {
//                SampleApplication.instance.resources.getStringArray(R.array.array_sdk_env)[0] -> "https://staging-pacheckoutdemo.airwallex.com/checkout-success?isTesting=Y"
//                SampleApplication.instance.resources.getStringArray(R.array.array_sdk_env)[1] -> "https://demo-pacheckoutdemo.airwallex.com/checkout-success?isTesting=Y"
//                SampleApplication.instance.resources.getStringArray(R.array.array_sdk_env)[2] -> "https://pacheckoutdemo.airwallex.com/checkout-success?isTesting=Y"
//                else -> throw Exception("Unsupported CheckoutMode: ${Settings.checkoutMode}")
//            }
        }

    private class CartItem constructor(
        order: PhysicalProduct,
        context: Context?,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        private val removeHandler: () -> Unit
    ) :
        RelativeLayout(context, attrs, defStyleAttr) {

        private val viewBinding = CartItemBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )

        init {
            viewBinding.tvProductName.text = order.name
            viewBinding.tvProductType.text = String.format("%s x %d", order.type, order.quantity)
            viewBinding.tvProductPrice.text =
                String.format("$%.2f", order.unitPrice ?: 0 * (order.quantity ?: 0))
            viewBinding.tvRemove.setOnSingleClickListener {
                removeHandler.invoke()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    private fun initializeProductsViews(products: MutableList<PhysicalProduct>) {
        viewBinding.llProducts.removeAllViews()
        products.map {
            CartItem(
                order = it,
                context = context,
                removeHandler = {
                    products.remove(it)
                    initializeProductsViews(products)
                }
            )
        }.forEach { viewBinding.llProducts.addView(it) }

        val subtotalPrice =
            products.sumByDouble { it.unitPrice ?: 0 * (it.quantity ?: 0).toDouble() }
        val shipping = 0
        val totalPrice = subtotalPrice + shipping

        viewBinding.tvOrderSubtotalPrice.text = String.format("$%.2f", subtotalPrice)
        viewBinding.tvOrderTotalPrice.text = String.format("$%.2f", totalPrice)
        viewBinding.tvShipping.text = getString(R.string.free)
        viewBinding.tvOrderSum.text = products.sumBy { it.quantity ?: 0 }.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.shippingItemView.renewalShipping(shipping)
        viewBinding.shippingItemView.onClickAction = {
            viewModel.presentShippingFlow(shipping).observe(viewLifecycleOwner) {
                when (it) {
                    is PaymentCartViewModel.ShippingResult.Success -> {
                        Log.d(TAG, "Save the shipping success")
                        viewBinding.shippingItemView.renewalShipping(it.shipping)
                        this@PaymentCartFragment.shipping = it.shipping
                    }
                    is PaymentCartViewModel.ShippingResult.Cancel -> {
                        Log.d(TAG, "User cancel edit shipping...")
                    }
                }
            }
        }
        initializeProductsViews(products.toMutableList())
        viewBinding.btnCheckout.setOnSingleClickListener {
            when (checkoutMode) {
                AirwallexCheckoutMode.PAYMENT -> {
                    startPaymentFlow()
                }
                AirwallexCheckoutMode.RECURRING -> {
                    startRecurringFlow()
                }
                AirwallexCheckoutMode.RECURRING_WITH_INTENT -> {
                    startRecurringWithIntentFlow()
                }
            }
        }
    }

    /**
     * one-off payment
     *
     */
    private fun startPaymentFlow() {
        viewLifecycleOwner.lifecycleScope.safeLaunch(Dispatchers.Main, coroutineExceptionHandler) {
            (activity as? PaymentCartActivity)?.setLoadingProgress(true)
            val paymentIntentResponse = withContext(Dispatchers.IO) {
                Settings.token = null
                val response = api.authentication(
                    apiKey = Settings.apiKey,
                    clientId = Settings.clientId
                )
                Settings.token = JSONObject(response.string())["token"].toString()

                val products = products
                val shipping = shipping
                val body = mutableMapOf(
                    "request_id" to UUID.randomUUID().toString(),
//                            "amount" to products.sumByDouble { product ->
//                                product.unitPrice ?: 0 * (product.quantity ?: 0).toDouble()
//                            },
                    "amount" to Settings.price.toDouble(),
                    "currency" to Settings.currency,
                    "merchant_order_id" to UUID.randomUUID().toString(),
                    "order" to PurchaseOrder.Builder()
                        .setProducts(products)
                        .setShipping(shipping)
                        .setType("physical_goods")
                        .build()
                        .toParamMap(),
                    "descriptor" to "Airwallex - T-sh  irt",
                    "metadata" to mapOf("id" to 1),
                    "email" to "yimadangxian@airwallex.com",
                    "return_url" to returnUrl,
                )
                Settings.cachedCustomerId?.let {
                    body.put("customer_id", it)
                }
                api.createPaymentIntent(body)
            }

            (activity as? PaymentCartActivity)?.setLoadingProgress(false)

            val paymentIntent =
                PaymentIntentParser().parse(JSONObject(paymentIntentResponse.string()))

            viewModel.presentPaymentFlow(
                AirwallexPaymentSession.Builder(paymentIntent).build()
            ).observe(viewLifecycleOwner) {
                when (it) {
                    is PaymentCartViewModel.PaymentFlowResult.Success -> {
                        showPaymentSuccess()
                    }
                    is PaymentCartViewModel.PaymentFlowResult.Error -> {
                        showPaymentError(it.exception.message)
                    }
                    is PaymentCartViewModel.PaymentFlowResult.WeChatPay -> {
                        startWeChatPay(it.weChat)
                    }
                    is PaymentCartViewModel.PaymentFlowResult.Redirect -> {
                        startRedirectUrl(it.redirectUrl)
                    }
                    is PaymentCartViewModel.PaymentFlowResult.Cancel -> {
                        Log.d(TAG, "User cancel the payment")
                        showPaymentCancelled()
                    }
                }
            }
        }
    }

    /**
     * Recurring flow
     */
    private fun startRecurringFlow() {
        viewLifecycleOwner.lifecycleScope.safeLaunch(Dispatchers.Main, coroutineExceptionHandler) {
            (activity as? PaymentCartActivity)?.setLoadingProgress(true)
            // Recurring flow require customer id
            val customerId = withContext(Dispatchers.IO) {
                if (TextUtils.isEmpty(Settings.cachedCustomerId)) {
                    val response = api.authentication(
                        apiKey = Settings.apiKey,
                        clientId = Settings.clientId
                    )
                    Settings.token = JSONObject(response.string())["token"].toString()

                    val customerResponse = api.createCustomer(
                        mutableMapOf(
                            "request_id" to UUID.randomUUID().toString(),
                            "merchant_customer_id" to UUID.randomUUID().toString(),
                            "first_name" to "John",
                            "last_name" to "Doe",
                            "email" to "john.doe@airwallex.com",
                            "phone_number" to "13800000000",
                            "additional_info" to mapOf(
                                "registered_via_social_media" to false,
                                "registration_date" to "2019-09-18",
                                "first_successful_order_date" to "2019-09-18"
                            ),
                            "metadata" to mapOf(
                                "id" to 1
                            )
                        )
                    )
                    val customerId = JSONObject(customerResponse.string())["id"].toString()
                    Settings.cachedCustomerId = customerId
                    customerId
                } else {
                    Settings.cachedCustomerId
                }
            }

            (activity as? PaymentCartActivity)?.setLoadingProgress(false)

            viewModel.presentPaymentFlow(
                AirwallexRecurringSession.Builder(
                    requireNotNull(customerId, { "CustomerId is required" }),
                    Settings.currency,
                    BigDecimal.valueOf(Settings.price.toDouble()),
                    nextTriggerBy
                )
                    .setShipping(shipping)
                    .build(),
                clientSecretProvider
            ).observe(viewLifecycleOwner) {
                when (it) {
                    is PaymentCartViewModel.PaymentFlowResult.Success -> {
                        showPaymentSuccess()
                    }
                    is PaymentCartViewModel.PaymentFlowResult.Error -> {
                        showPaymentError(it.exception.localizedMessage)
                    }
                    is PaymentCartViewModel.PaymentFlowResult.WeChatPay -> {
                        startWeChatPay(it.weChat)
                    }
                    is PaymentCartViewModel.PaymentFlowResult.Redirect -> {
                        startRedirectUrl(it.redirectUrl)
                    }
                    is PaymentCartViewModel.PaymentFlowResult.Cancel -> {
                        showPaymentCancelled()
                    }
                }
            }
        }
    }

    /**
     * Recurring with payment intent flow
     */
    private fun startRecurringWithIntentFlow() {
        viewLifecycleOwner.lifecycleScope.safeLaunch(Dispatchers.Main, coroutineExceptionHandler) {
            (activity as? PaymentCartActivity)?.setLoadingProgress(true)
            // Recurring flow require customer id
            val paymentIntentResponse = withContext(Dispatchers.IO) {
                val customerId = if (TextUtils.isEmpty(Settings.cachedCustomerId)) {
                    val response = api.authentication(
                        apiKey = Settings.apiKey,
                        clientId = Settings.clientId
                    )
                    Settings.token = JSONObject(response.string())["token"].toString()

                    val customerResponse = api.createCustomer(
                        mutableMapOf(
                            "request_id" to UUID.randomUUID().toString(),
                            "merchant_customer_id" to UUID.randomUUID().toString(),
                            "first_name" to "John",
                            "last_name" to "Doe",
                            "email" to "john.doe@airwallex.com",
                            "phone_number" to "13800000000",
                            "additional_info" to mapOf(
                                "registered_via_social_media" to false,
                                "registration_date" to "2019-09-18",
                                "first_successful_order_date" to "2019-09-18"
                            ),
                            "metadata" to mapOf(
                                "id" to 1
                            )
                        )
                    )
                    val customerId = JSONObject(customerResponse.string())["id"].toString()
                    Settings.cachedCustomerId = customerId
                    customerId
                } else {
                    Settings.cachedCustomerId
                }

                val products = products
                val shipping = shipping
                val body = mutableMapOf(
                    "request_id" to UUID.randomUUID().toString(),
//                            "amount" to products.sumByDouble { product ->
//                                product.unitPrice ?: 0 * (product.quantity ?: 0).toDouble()
//                            },
                    "amount" to Settings.price.toDouble(),
                    "currency" to Settings.currency,
                    "merchant_order_id" to UUID.randomUUID().toString(),
                    "order" to PurchaseOrder.Builder()
                        .setProducts(products)
                        .setShipping(shipping)
                        .setType("physical_goods")
                        .build()
                        .toParamMap(),
                    "customer_id" to requireNotNull(customerId),
                    "descriptor" to "Airwallex - T-sh  irt",
                    "metadata" to mapOf("id" to 1),
                    "email" to "yimadangxian@airwallex.com",
                    "return_url" to returnUrl,
                )
                api.createPaymentIntent(body)
            }

            (activity as? PaymentCartActivity)?.setLoadingProgress(false)
            val paymentIntent =
                PaymentIntentParser().parse(JSONObject(paymentIntentResponse.string()))
            viewModel.presentPaymentFlow(
                AirwallexRecurringWithIntentSession.Builder(
                    paymentIntent,
                    requireNotNull(
                        paymentIntent.customerId,
                        { "CustomerId is required" }
                    ),
                    nextTriggerBy
                ).build(),
                clientSecretProvider
            ).observe(viewLifecycleOwner) {
                when (it) {
                    is PaymentCartViewModel.PaymentFlowResult.Success -> {
                        showPaymentSuccess()
                    }
                    is PaymentCartViewModel.PaymentFlowResult.Error -> {
                        showPaymentError(it.exception.message)
                    }
                    is PaymentCartViewModel.PaymentFlowResult.WeChatPay -> {
                        startWeChatPay(it.weChat)
                    }
                    is PaymentCartViewModel.PaymentFlowResult.Redirect -> {
                        startRedirectUrl(it.redirectUrl)
                    }
                    is PaymentCartViewModel.PaymentFlowResult.Cancel -> {
                        Log.d(TAG, "User cancel the payment")
                        showPaymentCancelled()
                    }
                }
            }
        }
    }

    private fun startWeChatPay(weChat: WeChat) {
        Log.d(TAG, "weChat info $weChat")
        (activity as? PaymentCartActivity)?.setLoadingProgress(true)
        val prepayId = weChat.prepayId
        // We use the `URL mock` method to simulate WeChat Pay in the `Staging` environment.
        // By requesting this URL, we will set the status of the `PaymentIntent` to success.
        if (prepayId?.startsWith("http") == true) {
            // **This is just for test on Staging env**
            Log.d(
                TAG,
                "Confirm PaymentIntent success, MOCK WeChat Pay on staging env."
            )
            // MOCK WeChat Pay
            val client = OkHttpClient()
            val builder = Request.Builder()
            builder.url(prepayId)
            client.newCall(builder.build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    activity?.runOnUiThread {
                        Log.e(TAG, "Mock WeChat Pay failed, reason: $e.message")
                        showPaymentError(e.message)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    activity?.runOnUiThread {
                        if (response.isSuccessful) {
                            Log.d(TAG, "Mock WeChat Pay successful.")
                            showPaymentSuccess()
                        } else {
                            Log.e(TAG, "Mock WeChat Pay failed.")
                            showPaymentError("Mock WeChat Pay failed.")
                        }
                    }
                }
            })
        } else {
            Log.d(TAG, "Confirm PaymentIntent success, launch REAL WeChat Pay.")
            // Launch WeChat Pay
            WXPay.instance.launchWeChat(
                data = weChat,
                listener = object : WXPay.WeChatPaymentListener {
                    override fun onSuccess() {
                        Log.d(TAG, "REAL WeChat Pay successful.")
                        showPaymentSuccess()
                    }

                    override fun onFailure(
                        errCode: String?,
                        errMessage: String?
                    ) {
                        Log.e(
                            TAG,
                            "REAL WeChat Pay failed, reason: $errMessage"
                        )
                        showPaymentError(errMessage)
                    }

                    override fun onCancel() {
                        Log.d(TAG, "REAL WeChat Pay cancelled.")
                        showPaymentError("REAL WeChat Pay cancelled.")
                    }
                }
            )
        }
    }

    private fun startRedirectUrl(redirectUrl: String) {
        try {
            airwallex.handleAction(redirectUrl)
        } catch (e: RedirectException) {
            showPaymentError(e.localizedMessage)
        }
    }

    /**
     * After successful payment, Airwallex server will notify the Merchant,
     * Then Merchant can retrieve the `PaymentIntent` to check the `status` of the PaymentIntent.
     */
    private fun retrievePaymentIntent(
        airwallex: Airwallex,
        paymentIntentId: String,
        clientSecret: String
    ) {
        airwallex.retrievePaymentIntent(
            params = RetrievePaymentIntentParams(
                // the ID of the `PaymentIntent`, required.
                paymentIntentId = paymentIntentId,
                // the clientSecret of `PaymentIntent`, required.
                clientSecret = clientSecret
            ),
            listener = object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onSuccess(response: PaymentIntent) {
                    if (response.status == PaymentIntentStatus.SUCCEEDED) {
                        // payment successful
                        showPaymentSuccess()
                    } else {
                        showPaymentError(response.status?.value)
                    }
                }

                override fun onFailed(exception: Exception) {
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // We need to handle activity result
        airwallex.handlePaymentData(requestCode, resultCode, data)
    }

    private fun showPaymentSuccess() {
        (activity as? PaymentCartActivity)?.setLoadingProgress(false)
        (activity as? PaymentCartActivity)?.showAlert(
            getString(R.string.payment_successful),
            getString(R.string.payment_successful_message)
        )
    }

    private fun showCreatePaymentIntentError(error: String? = null) {
        (activity as? PaymentCartActivity)?.setLoadingProgress(false)
        (activity as? PaymentCartActivity)?.showAlert(
            getString(R.string.create_payment_intent_failed),
            error ?: getString(R.string.payment_failed_message)
        )
    }

    private fun showPaymentError(error: String? = null) {
        (activity as? PaymentCartActivity)?.setLoadingProgress(false)
        (activity as? PaymentCartActivity)?.showAlert(
            getString(R.string.payment_failed),
            error ?: getString(R.string.payment_failed_message)
        )
    }

    private fun showSelectPaymentError(error: String? = null) {
        (activity as? PaymentCartActivity)?.setLoadingProgress(false)
        (activity as? PaymentCartActivity)?.showAlert(
            getString(R.string.select_payment_failed),
            error ?: getString(R.string.payment_failed_message)
        )
    }

    private fun showPaymentCancelled(error: String? = null) {
        (activity as? PaymentCartActivity)?.setLoadingProgress(false)
        (activity as? PaymentCartActivity)?.showAlert(
            getString(R.string.payment_cancelled),
            error ?: getString(R.string.payment_cancelled_message)
        )
    }

    companion object {
        private const val TAG = "PaymentCartFragment"
    }
}
