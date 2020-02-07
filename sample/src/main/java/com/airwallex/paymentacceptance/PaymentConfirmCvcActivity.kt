package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.airwallex.android.Airwallex
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import kotlinx.android.synthetic.main.activity_confirm_cvc.*
import kotlinx.android.synthetic.main.activity_confirm_cvc.loading
import kotlinx.android.synthetic.main.activity_confirm_cvc.rlPlay
import kotlinx.android.synthetic.main.activity_confirm_cvc.toolbar
import java.util.*

class PaymentConfirmCvcActivity : PaymentBaseActivity() {

    private val paymentMethod: PaymentMethod by lazy {
        intent.getParcelableExtra(PAYMENT_METHOD) as PaymentMethod
    }

    private val paymentIntentId: String by lazy {
        intent.getStringExtra(PAYMENT_INTENT_ID)
    }

    companion object {

        private const val TAG = "ConfirmCvcActivity"

        fun startActivityForResult(
            activity: Activity,
            paymentMethod: PaymentMethod?,
            paymentIntentId: String,
            requestCode: Int
        ) {
            val intent = Intent(activity, PaymentConfirmCvcActivity::class.java)
            intent.putExtra(PAYMENT_METHOD, paymentMethod)
            intent.putExtra(PAYMENT_INTENT_ID, paymentIntentId)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_cvc)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.cvc_hint)
        }

        tvTitle.text = getString(
            R.string.enter_cvc_title,
            paymentMethod.card?.brand?.capitalize(),
            paymentMethod.card?.last4
        )

        rlPlay.isEnabled = false
        atlCardCvc.completionCallback = {
            rlPlay.isEnabled = true
        }

        rlPlay.setOnClickListener {
            if (atlCardCvc.value.isEmpty()) {
                atlCardCvc.error = resources.getString(R.string.empty_cvc)
                return@setOnClickListener
            }

            if (!atlCardCvc.isValid) {
                atlCardCvc.error = resources.getString(R.string.invalid_cvc)
                return@setOnClickListener
            }

            loading.visibility = View.VISIBLE
            startConfirmPaymentIntent(paymentMethod, atlCardCvc.value)
            return@setOnClickListener
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startConfirmPaymentIntent(paymentMethod: PaymentMethod, cvc: String) {
        val paymentIntentParams: PaymentIntentParams
        val device = Device.Builder()
            .setBrowserInfo("Chrome/76.0.3809.100")
            .setCookiesAccepted("true")
            .setDeviceId("IMEI-4432fsdafd31243244fdsafdfd653")
            .setHostName("www.airwallex.com")
            .setHttpBrowserEmail("jim631@sina.com")
            .setHttpBrowserType("chrome")
            .setIpAddress("123.90.0.1")
            .setIpNetworkAddress("128.0.0.0")
            .build()

        val paymentMethodOptions: PaymentMethodOptions = PaymentMethodOptions.Builder()
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


        paymentIntentParams = PaymentIntentParams.Builder()
            .setRequestId(UUID.randomUUID().toString())
            .setDevice(device)
            .setPaymentMethodReference(
                PaymentMethodReference.Builder()
                    .setId(paymentMethod.id)
                    .setCvc(cvc)
                    .build()
            )
            .setPaymentMethodOptions(paymentMethodOptions)
            .build()

        // Start Confirm PaymentIntent
        val airwallex = Airwallex(Store.token)
        airwallex.confirmPaymentIntent(
            paymentIntentId = paymentIntentId,
            paymentIntentParams = paymentIntentParams,
            callback = object : Airwallex.PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    retrievePaymentIntent(airwallex)
                }

                override fun onFailed(exception: AirwallexException) {
                    loading.visibility = View.GONE
                    Toast.makeText(
                        this@PaymentConfirmCvcActivity,
                        exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        )
    }

    private fun retrievePaymentIntent(airwallex: Airwallex) {
        Log.d(
            TAG,
            "Start retrieve PaymentIntent $paymentIntentId"
        )
        airwallex.retrievePaymentIntent(
            paymentIntentId = paymentIntentId,
            callback = object : Airwallex.PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    Log.d(
                        TAG,
                        "Retrieve PaymentIntent success, PaymentIntent status: ${paymentIntent.status}"
                    )

                    loading.visibility = View.GONE
                    if (paymentIntent.status == "SUCCEEDED") {
                        showPaymentSuccessDialog()
                    } else {
                        showPaymentFailedDialog()
                    }
                }

                override fun onFailed(exception: AirwallexException) {
                    Log.e(TAG, "Retrieve PaymentIntent failed")
                    loading.visibility = View.GONE
                    // TODO Need Retry?

                    showPaymentFailedDialog()
                }
            })
    }

    private fun showPaymentSuccessDialog() {
        AlertDialog.Builder(this@PaymentConfirmCvcActivity)
            .setTitle(R.string.payment_successful)
            .setMessage(R.string.payment_successful_message)
            .setNegativeButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                notifyPaymentSuccess()
            }
            .show()
    }

    private fun showPaymentFailedDialog() {
        AlertDialog.Builder(this@PaymentConfirmCvcActivity)
            .setTitle(R.string.payment_failed)
            .setMessage(R.string.payment_failed_message)
            .setNegativeButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}