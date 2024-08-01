package com.airwallex.paymentacceptance.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.postDelayed(
            {
                MainActivity.startActivity(this@SplashActivity)
                overridePendingTransition(0, android.R.anim.fade_out)
            },
            1000
        )
    }
}