package com.airwallex.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_payment_methods.*

class PaymentStartPayActivity : AppCompatActivity() {

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, PaymentStartPayActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_methods)

        btnPay.setOnClickListener {


        }
    }
}