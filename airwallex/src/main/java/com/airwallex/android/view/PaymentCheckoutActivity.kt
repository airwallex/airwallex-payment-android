package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.airwallex.android.Airwallex
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import kotlinx.android.synthetic.main.activity_airwallex.*
import kotlinx.android.synthetic.main.activity_payment_checkout.*
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
            paymentIntent.clientSecret!!
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

        tvTotalPrice.text = String.format(
            "%s%.2f",
            Currency.getInstance(paymentIntent.currency).symbol,
            paymentIntent.amount
        )
        paymentMethodItemView.renewalPaymentMethod(paymentMethod, args.cvc)
        paymentMethodItemView.cvcChangedCallback = {
            updateButtonStatus()
        }

        rlPayNow.setOnClickListener {
            startConfirmPaymentIntent(paymentMethod)
        }
        updateButtonStatus()
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
                    finishWithPaymentIntent(paymentIntent, paymentMethod.type)
                }

                override fun onFailed(exception: AirwallexException) {
                    loading.visibility = View.GONE
                    alert(
                        getString(R.string.payment_failed),
                        getString(R.string.payment_failed_message)
                    )
                }
            }
        )
    }

    private fun finishWithPaymentIntent(paymentIntent: PaymentIntent, type: PaymentMethodType) {
        loading.visibility = View.GONE
        setResult(
            Activity.RESULT_OK, Intent()
                .putExtras(PaymentCheckoutActivityStarter.Result(paymentIntent, type).toBundle())
        )
        finish()
    }

    private fun updateButtonStatus() {
        rlPayNow.isEnabled = paymentMethodItemView.isValid
    }
}