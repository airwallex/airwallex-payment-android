package com.airwallex.paymentacceptance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

abstract class PaymentBaseActivity : AppCompatActivity() {

    private val localBroadcastManager: LocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(this)
    }

    companion object {
        const val PAYMENT_METHOD = "PAYMENT_METHOD"
        const val SHIPPING_DETAIL = "SHIPPING_DETAIL"
        const val PAYMENT_INTENT_ID = "payment_intent_id"

        const val REQUEST_EDIT_SHIPPING_CODE = 8
        const val REQUEST_PAYMENT_METHOD_CODE = 9

        const val REQUEST_CONFIRM_CVC_CODE = 998

        const val PAYMENT_SUCCESS_ACTION = "PAYMENT_SUCCESS_ACTION"
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

    fun notifyPaymentSuccess() {
        localBroadcastManager.sendBroadcast(Intent().setAction(PAYMENT_SUCCESS_ACTION))
    }

    private fun registerPaymentBroadcast() {
        val intentFilter = IntentFilter(PAYMENT_SUCCESS_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    private fun unRegisterLoginBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPaymentBroadcast()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        unRegisterLoginBroadcast()
        super.onDestroy()
    }
}