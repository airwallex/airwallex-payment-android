package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

abstract class PaymentBaseActivity : AppCompatActivity() {

    abstract val inPaymentFlow: Boolean

    companion object {
        const val PAYMENT_METHOD = "payment_method"
        const val SHIPPING_DETAIL = "shipping_detail"
        const val PAYMENT_INTENT = "payment_intent"

        const val REQUEST_EDIT_SHIPPING_CODE = 8
        const val REQUEST_PAYMENT_METHOD_CODE = 9

        const val REQUEST_CONFIRM_CVC_CODE = 998

        const val PAYMENT_SUCCESS_ACTION = "PAYMENT_SUCCESS_ACTION"
    }

    private val localBroadcastManager: LocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(this)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

    private fun notifyPaymentSuccess() {
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
        if (inPaymentFlow) {
            registerPaymentBroadcast()
        }
    }

    override fun onDestroy() {
        if (inPaymentFlow) {
            unRegisterLoginBroadcast()
        }
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showPaymentSuccess() {
        showAlert(
            getString(R.string.payment_successful),
            getString(R.string.payment_successful_message)
        ) {
            notifyPaymentSuccess()
        }
    }

    fun showPaymentError() {
        showAlert(
            getString(R.string.payment_failed),
            getString(R.string.payment_failed_message)
        )
    }

    private fun showAlert(title: String, message: String, completion: (() -> Unit)? = null) {
        if (!isFinishing) {
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    completion?.invoke()
                }
                .create()
                .show()
        }
    }
}