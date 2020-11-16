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
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexStarter
import com.airwallex.android.RetrievePaymentIntentParams
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import com.airwallex.android.model.Address
import com.airwallex.android.model.parser.PaymentIntentParser
import kotlinx.android.synthetic.main.cart_item.view.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import kotlin.coroutines.CoroutineContext

class PaymentCartFragment : Fragment() {

    private val clientSecretProvider by lazy {
        ExampleClientSecretProvider()
    }

    private val airwallexStarter by lazy {
        AirwallexStarter(this)
    }

    private val authApi: AuthApi
        get() {
            if (TextUtils.isEmpty(Settings.authUrl)) {
                throw IllegalArgumentException("Auth url should not be null or empty")
            }
            return ApiFactory(Settings.authUrl).buildRetrofit().create(AuthApi::class.java)
        }

    private val api: Api
        get() {
            if (TextUtils.isEmpty(Settings.baseUrl)) {
                throw IllegalArgumentException("Base url should not be null or empty")
            }
            return ApiFactory(Settings.baseUrl).buildRetrofit().create(Api::class.java)
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

    private class CartItem constructor(
        order: PhysicalProduct,
        context: Context?,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        private val removeHandler: () -> Unit
    ) :
        RelativeLayout(context, attrs, defStyleAttr) {

        init {
            View.inflate(
                context,
                R.layout.cart_item, this
            )

            tvProductName.text = order.name
            tvProductType.text = String.format("%s x %d", order.type, order.quantity)
            tvProductPrice.text =
                String.format("$%.2f", order.unitPrice ?: 0 * (order.quantity ?: 0))
            tvRemove.setOnClickListener {
                removeHandler.invoke()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(
            context,
            R.layout.fragment_cart, null
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shippingItemView.renewalShipping(shipping)
        shippingItemView.onClickAction = {
            airwallexStarter.presentShippingFlow(
                shipping,
                object : AirwallexStarter.PaymentShippingListener {
                    override fun onSuccess(shipping: Shipping) {
                        Log.d(TAG, "Save the shipping success")
                        shippingItemView.renewalShipping(shipping)
                        this@PaymentCartFragment.shipping = shipping
                    }

                    override fun onCancelled() {
                        Log.d(TAG, "User cancel edit shipping...")
                    }
                })
        }
        initializeProductsViews(products.toMutableList())
        btnCheckout.setOnClickListener {
            authAndCreatePaymentIntent()
        }
    }

    private fun initializeProductsViews(products: MutableList<PhysicalProduct>) {
        llProducts.removeAllViews()
        products.map {
            CartItem(
                order = it,
                context = context,
                removeHandler = {
                    products.remove(it)
                    initializeProductsViews(products)
                }
            )
        }.forEach { llProducts.addView(it) }

        val subtotalPrice =
            products.sumByDouble { it.unitPrice ?: 0 * (it.quantity ?: 0).toDouble() }
        val shipping = 0
        val totalPrice = subtotalPrice + shipping

        tvOrderSubtotalPrice.text = String.format("$%.2f", subtotalPrice)
        tvOrderTotalPrice.text = String.format("$%.2f", totalPrice)
        tvShipping.text = getString(R.string.free)
        tvOrderSum.text = products.sumBy { it.quantity ?: 0 }.toString()
    }

    /**
     * `IMPORTANT` This code must be placed on the merchant server, this is just for Demo purposes only
     */
    private fun authAndCreatePaymentIntent() {
        (activity as? PaymentCartActivity)?.setLoadingProgress(true)
        Settings.token = null
        viewLifecycleOwner.lifecycleScope.safeLaunch(Dispatchers.IO) {
            val response = authApi.authentication(
                apiKey = Settings.apiKey,
                clientId = Settings.clientId
            )
            Settings.token = JSONObject(response.string())["token"].toString()

            val customerId = if (TextUtils.isEmpty(Settings.cachedCustomerId)) {
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
            val paymentIntentResponse = api.createPaymentIntent(
                mutableMapOf(
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
                    "customer_id" to customerId,
                    "descriptor" to "Airwallex - T-shirt",
                    "metadata" to mapOf("id" to 1)
                )
            )
            withContext(Dispatchers.Main) {
                handlePaymentIntentResponse(paymentIntentResponse)
            }
        }
    }

    /**
     * PaymentIntent must come from merchant's server
     */
    private fun handlePaymentIntentResponse(responseBody: ResponseBody) {
        val paymentIntent = PaymentIntentParser().parse(JSONObject(responseBody.string()))
        airwallexStarter.presentPaymentFlow(
            paymentIntent,
            clientSecretProvider,
            object : AirwallexStarter.PaymentIntentListener {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    when (paymentIntent.paymentMethodType) {
                        PaymentMethodType.WECHAT -> {
                            val weChat = paymentIntent.weChat
                            if (weChat == null) {
                                showPaymentError("Server error, WeChat data is null...")
                                return
                            }

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
                                    context = activity!!,
                                    appId = Settings.weChatAppId,
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
                                    })
                            }
                        }
                        PaymentMethodType.CARD -> {
                            showPaymentSuccess()
                        }
                    }
                }

                override fun onFailed(error: AirwallexError) {
                    showPaymentError(error.message)
                }

                override fun onCancelled() {
                    Log.d(TAG, "User cancel the payment")
                    showPaymentCancelled()
                }
            })
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is HttpException) {
            showCreatePaymentIntentError(
                throwable.response()?.errorBody()?.string() ?: throwable.localizedMessage
            )
        } else {
            showCreatePaymentIntentError(throwable.localizedMessage)
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
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                }
            })
    }

    override fun onDestroy() {
        airwallexStarter.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airwallexStarter.onActivityResult(requestCode, resultCode, data)
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

    private fun showPaymentCancelled(error: String? = null) {
        (activity as? PaymentCartActivity)?.setLoadingProgress(false)
        (activity as? PaymentCartActivity)?.showAlert(
            getString(R.string.payment_cancelled),
            error ?: getString(R.string.payment_cancelled_message)
        )
    }

    private fun CoroutineScope.safeLaunch(
        customContext: CoroutineContext? = null,
        work: suspend CoroutineScope.() -> Unit
    ): Job {
        return if (customContext == null) launch(
            context = coroutineExceptionHandler,
            block = work
        ) else launch(context = coroutineExceptionHandler) {
            launch(context = customContext, block = work)
        }
    }

    companion object {
        private const val TAG = "PaymentCartFragment"
    }
}
