package com.airwallex.android.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.airwallex.android.R
import com.airwallex.android.model.PaymentMethod
import java.util.*

internal class DeletePaymentMethodDialogFactory internal constructor(
    private val context: Context,
    private val adapter: PaymentMethodsAdapter,
    private val onDeletedCallback: (PaymentMethod) -> Unit
) {
    @SuppressLint("StringFormatInvalid")
    @JvmSynthetic
    fun create(paymentMethod: PaymentMethod): AlertDialog {
        val title = paymentMethod.card?.let {
            context.resources.getString(
                R.string.delete_payment_method_prompt_title,
                String.format("%s •••• %s", it.brand?.toUpperCase(Locale.ROOT), it.last4)
            )
        }
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton(R.string.delete_payment_method_positive) { _, _ ->
                onDeletedPaymentMethod(paymentMethod)
            }
            .setNegativeButton(R.string.delete_payment_method_negative) { _, _ ->
                adapter.resetPaymentMethod(paymentMethod)
            }
            .setOnCancelListener {
                adapter.resetPaymentMethod(paymentMethod)
            }
            .create()
    }

    @JvmSynthetic
    internal fun onDeletedPaymentMethod(paymentMethod: PaymentMethod) {
        onDeletedCallback(paymentMethod)
    }
}
