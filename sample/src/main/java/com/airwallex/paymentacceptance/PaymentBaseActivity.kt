package com.airwallex.paymentacceptance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

abstract class PaymentBaseActivity : AppCompatActivity() {

    abstract val inPaymentFlow: Boolean

    companion object {
        private const val PAYMENT_SUCCESS_ACTION = "PAYMENT_SUCCESS_ACTION"
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
        if (item.itemId == android.R.id.home) {
            onBackPressed()
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

    fun showError(title: String, message: String) {
        showAlert(title, message)
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