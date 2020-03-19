package com.airwallex.paymentacceptance

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PaymentConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, PaymentConfigFragment())
            .commit()
    }
}