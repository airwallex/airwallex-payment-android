package com.airwallex.android.view

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.R
import kotlinx.android.synthetic.main.activity_airwallex.*

abstract class AirwallexActivity : AppCompatActivity() {

    companion object {
        const val TOKEN = "token"
        const val CLIENT_SECRET = "client_secret"
        const val PAYMENT_BILLING = "billing"
        const val SAME_AS_SHIPPING = "same_as_shipping"
        const val REQUEST_ADD_CARD_CODE = 98
        const val REQUEST_ADD_BILLING_CODE = 99

        const val PAYMENT_METHOD = "payment_method"
        const val PAYMENT_CARD_CVC = "payment_card_cvc"
        const val PAYMENT_INTENT = "payment_intent"
    }

    protected abstract fun onActionSave()

    protected abstract fun isValid(): Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_airwallex)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun showError(message: String) {
        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }
    }
}