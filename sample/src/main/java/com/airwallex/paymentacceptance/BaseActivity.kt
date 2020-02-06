package com.airwallex.paymentacceptance

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    companion object {
        const val PAYMENT_METHOD = "PAYMENT_METHOD"
        const val SHIPPING_DETAIL = "SHIPPING_DETAIL"
        const val PAYMENT_INTENT_ID = "payment_intent_id"

        const val REQUEST_EDIT_SHIPPING_CODE = 8
        const val REQUEST_PAYMENT_METHOD_CODE = 9

        const val REQUEST_EDIT_CARD_CODE = 98

        const val REQUEST_CONFIRM_CVC_CODE = 998
    }
}