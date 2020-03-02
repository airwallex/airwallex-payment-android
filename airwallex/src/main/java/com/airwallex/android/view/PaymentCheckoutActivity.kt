package com.airwallex.android.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.airwallex.android.Airwallex
import com.airwallex.android.Logger
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import kotlinx.android.synthetic.main.activity_airwallex.*
import kotlinx.android.synthetic.main.activity_payment_checkout.*
import okhttp3.*
import java.io.IOException
import java.util.*

internal class PaymentCheckoutActivity : AirwallexActivity() {

    private val device = Device.Builder()
        .setBrowserInfo("Chrome/76.0.3809.100")
        .setCookiesAccepted("true")
        .setDeviceId("IMEI-4432fsdafd31243244fdsafdfd653")
        .setHostName("www.airwallex.com")
        .setHttpBrowserEmail("jim631@sina.com")
        .setHttpBrowserType("chrome")
        .setIpAddress("123.90.0.1")
        .setIpNetworkAddress("128.0.0.0")
        .build()


    private val airwallex: Airwallex by lazy {
        Airwallex(
            args.customerSessionConfig.token,
            paymentIntent.clientSecret
        )
    }

    private val paymentMethod: PaymentMethod
        get() {
            return args.paymentMethod
        }

    private val paymentIntent: PaymentIntent
        get() {
            return args.customerSessionConfig.paymentIntent
        }

    private val args: PaymentCheckoutActivityStarter.Args by lazy {
        PaymentCheckoutActivityStarter.Args.getExtra(intent)
    }

    override fun onActionSave() {

    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_payment_checkout
        viewStub.inflate()

        tvTotalPrice.text = String.format("$%.2f", paymentIntent.amount)
        paymentMethodItemView.renewalPaymentMethod(paymentMethod, args.cvc)
        paymentMethodItemView.cvcChangedCallback = {
            updateButtonStatus()
        }

        rlPayNow.setOnClickListener {
            startConfirmPaymentIntent(paymentMethod)
        }
    }

    private fun startConfirmPaymentIntent(paymentMethod: PaymentMethod) {
        loading.visibility = View.VISIBLE
        val paymentIntentParams: PaymentIntentParams = when (paymentMethod.type) {
            PaymentMethodType.CARD -> {
                PaymentIntentParams.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setCustomerId(paymentIntent.customerId)
                    .setDevice(device)
                    .setPaymentMethodReference(
                        PaymentMethodReference.Builder()
                            .setId(paymentMethod.id)
                            .setCvc("123")
                            .build()
                    )
                    .setPaymentMethodOptions(
                        PaymentMethodOptions.Builder()
                            .setCardOptions(
                                PaymentMethodOptions.CardOptions.Builder()
                                    .setAutoCapture(true)
                                    .setThreeDs(
                                        PaymentMethodOptions.CardOptions.ThreeDs.Builder()
                                            .setOption(false)
                                            .build()
                                    ).build()
                            )
                            .build()
                    )
                    .build()
            }
            PaymentMethodType.WECHAT -> {
                PaymentIntentParams.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setCustomerId(paymentIntent.customerId)
                    .setDevice(device)
                    .setPaymentMethod(paymentMethod)
                    .build()
            }
        }

        // Start Confirm PaymentIntent
        airwallex.confirmPaymentIntent(
            paymentIntentId = paymentIntent.id,
            paymentIntentParams = paymentIntentParams,
            callback = object : Airwallex.PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    Logger.debug("Confirm Payment Intent Success!")
                    handlePaymentResult(paymentMethod, paymentIntent) {
                        retrievePaymentIntent(airwallex)
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                    Logger.error("Confirm Payment Intent Failed!", exception)
                    loading.visibility = View.GONE
                    alert(
                        getString(R.string.payment_failed),
                        getString(R.string.payment_failed_message)
                    )
                }
            }
        )
    }

    private fun handlePaymentResult(
        paymentMethod: PaymentMethod,
        paymentIntent: PaymentIntent,
        completion: () -> Unit
    ) {
        when (paymentMethod.type) {
            PaymentMethodType.CARD -> {
                completion.invoke()
            }
            PaymentMethodType.WECHAT -> {
                val nextAction = paymentIntent.nextAction
                if (nextAction?.data == null
                ) {
                    Toast.makeText(
                        this@PaymentCheckoutActivity,
                        "Server error, NextAction is null...",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val prepayId = nextAction.data.prepayId
                if (prepayId?.startsWith("http") == true) {
                    Logger.debug("Confirm PaymentIntent success, launch MOCK Wechat pay.")
                    // launch mock wechat pay
                    val client = OkHttpClient()
                    val builder = Request.Builder()
                    builder.url(prepayId)
                    client.newCall(builder.build()).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Logger.error("User cancel the Wechat payment")
                            loading.visibility = View.GONE
                            Toast.makeText(
                                this@PaymentCheckoutActivity,
                                "Failed to mock wechat pay, reason: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            completion.invoke()
                        }
                    })
                } else {
                    Logger.debug("Confirm PaymentIntent success, launch REAL Wechat pay.")
                    val data = paymentIntent.nextAction?.data
                    if (data == null) {
                        Toast.makeText(
                            this@PaymentCheckoutActivity,
                            "No Wechat data!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    // launch wechat pay
//                    WXPay.instance.launchWeChat(
//                        context = this@PaymentCheckoutActivity,
//                        appId = Constants.APP_ID,
//                        data = data,
//                        listener = object : WXPay.WechatPaymentListener {
//                            override fun onSuccess() {
//                                completion.invoke()
//                            }
//
//                            override fun onFailure(errCode: String?, errMessage: String?) {
//                                Log.e(TAG, "Wechat pay failed, error $errMessage")
//                                loading.visibility = View.GONE
//                                Toast.makeText(
//                                    this@PaymentCheckoutActivity,
//                                    "errCode $errCode, errMessage $errMessage",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//
//                            override fun onCancel() {
//                                Log.e(TAG, "User cancel the Wechat payment")
//                                loading.visibility = View.GONE
//                                Toast.makeText(
//                                    this@PaymentCheckoutActivity,
//                                    "User cancel the payment",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        })
                }
            }
        }
    }

    private fun retrievePaymentIntent(airwallex: Airwallex) {
        Logger.debug(
            "Start retrieve PaymentIntent ${args.customerSessionConfig.paymentIntent.id}"
        )
        airwallex.retrievePaymentIntent(
            paymentIntentId = paymentIntent.id,
            callback = object : Airwallex.PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    Logger.debug(
                        "Retrieve PaymentIntent success, PaymentIntent status: ${paymentIntent.status}"
                    )

                    loading.visibility = View.GONE
                    if (paymentIntent.status == "SUCCEEDED") {
                        showPaymentSuccess()
                    } else {
                        showPaymentError()
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                    Logger.error("Retrieve PaymentIntent failed")
                    loading.visibility = View.GONE
                    showPaymentError()
                }
            })
    }

    fun showPaymentSuccess() {
        alert(
            getString(R.string.payment_successful),
            getString(R.string.payment_successful_message)
        ) {
            // Notify payment success
        }
    }

    fun showPaymentError() {
        alert(
            getString(R.string.payment_failed),
            getString(R.string.payment_failed_message)
        )
    }

    private fun updateButtonStatus() {
        rlPayNow.isEnabled = paymentMethodItemView.isValid
    }
}