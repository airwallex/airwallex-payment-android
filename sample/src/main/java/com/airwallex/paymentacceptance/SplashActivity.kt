package com.airwallex.paymentacceptance

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.postDelayed({
            PaymentCartActivity.startActivity(this@SplashActivity)
            overridePendingTransition(0, android.R.anim.fade_out)
        }, 1000)
    }
}
