package com.airwallex.paymentacceptance.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.databinding.ActivityMainBinding
import com.airwallex.paymentacceptance.ui.base.startActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnPaymentSdk.setOnClickListener {
            startActivity(PaymentIntegrationActivity::class)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        viewBinding.btnBillingSdk.setOnClickListener {
            startActivity(BillingIntegrationActivity::class)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
