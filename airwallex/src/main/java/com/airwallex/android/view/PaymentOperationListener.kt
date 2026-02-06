package com.airwallex.android.view

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.ui.R

interface PaymentOperationListener {
    fun onLoadingStateChanged(isLoading: Boolean)
    fun onPaymentResult(status: AirwallexPaymentStatus)
    fun onError(exception: Throwable, activity: ComponentActivity) {
        if (!activity.isFinishing) {
            AlertDialog.Builder(activity)
                .setTitle("")
                .setMessage(exception.message ?: exception.toString())
                .setCancelable(false)
                .setPositiveButton(R.string.airwallex_okay) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }
    }
}