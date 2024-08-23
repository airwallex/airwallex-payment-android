package com.airwallex.paymentacceptance.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.paymentacceptance.ui.base.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.postDelayed(
            {
                startActivity(MainActivity::class)
                overridePendingTransition(0, android.R.anim.fade_out)
                finish()
            },
            1000
        )
    }
}
