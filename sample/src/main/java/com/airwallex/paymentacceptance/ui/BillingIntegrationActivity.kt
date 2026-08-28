package com.airwallex.paymentacceptance.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.paymentacceptance.databinding.ActivityBillingIntegrationBinding

class BillingIntegrationActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityBillingIntegrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityBillingIntegrationBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}
