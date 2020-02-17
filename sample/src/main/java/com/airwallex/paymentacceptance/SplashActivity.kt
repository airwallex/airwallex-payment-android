package com.airwallex.paymentacceptance

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            PaymentCartActivity.startActivity(this@SplashActivity)
            overridePendingTransition(0, 0)
        }, 1000)
    }
}