package com.airwallex.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.Airwallex
import kotlinx.android.synthetic.main.activity_payment_methods.*

class PaymentStartPayActivity : AppCompatActivity() {

    private val paymentIntentId: String by lazy {
        intent.getStringExtra(PAYMENT_INTENT_ID)
    }

    private val token: String by lazy {
        intent.getStringExtra(PAYMENT_TOKEN)
    }

    companion object {

        private const val PAYMENT_INTENT_ID = "payment_intent_id"
        private const val PAYMENT_TOKEN = "payment_token"

        fun start(activity: Activity, paymentIntentId: String, token: String) {
            val intent = Intent(activity, PaymentStartPayActivity::class.java)
            intent.putExtra(PAYMENT_INTENT_ID, paymentIntentId)
            intent.putExtra(PAYMENT_TOKEN, token)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_methods)

        btnPay.setOnClickListener {

            val airwallex = Airwallex(this, token)
            airwallex.confirmPayment(paymentIntentId)
        }
    }
}