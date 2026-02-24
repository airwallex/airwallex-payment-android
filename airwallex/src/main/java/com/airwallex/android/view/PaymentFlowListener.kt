package com.airwallex.android.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AlertDialog
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.ui.R

interface PaymentFlowListener {
    fun onLoadingStateChanged(isLoading: Boolean, context: Context) {
        var currentContext: Context? = context
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) {
                if (currentContext.isFinishing) {
                    return
                }
                break
            }
            currentContext = currentContext.baseContext
        }

        if (isLoading) {
            AirwallexLoadingDialog.show(context)
        } else {
            AirwallexLoadingDialog.hide()
        }
    }

    fun onPaymentResult(status: AirwallexPaymentStatus)
    fun onError(exception: Throwable, context: Context) {
        var currentContext: Context? = context
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) {
                if (currentContext.isFinishing) {
                    return
                }
                break
            }
            currentContext = currentContext.baseContext
        }

        AlertDialog.Builder(context)
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

/**
 * Singleton to manage loading dialog state across payment flow.
 * Ensures only one loading dialog is shown at a time.
 */
internal object AirwallexLoadingDialog {
    private var dialog: ProgressDialog? = null

    fun show(context: Context) {
        if (dialog?.isShowing == true) return

        dialog = ProgressDialog(context).apply {
            setMessage("Loading...")
            setCancelable(false)
            show()
        }
    }

    fun hide() {
        dialog?.dismiss()
        dialog = null
    }
}