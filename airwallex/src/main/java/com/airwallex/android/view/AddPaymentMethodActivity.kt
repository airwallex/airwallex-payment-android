package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.Airwallex
import com.airwallex.android.CreatePaymentMethodParams
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.PaymentMethod
import kotlinx.android.synthetic.main.activity_add_card.*

/**
 * Activity to add new payment method
 */
internal class AddPaymentMethodActivity : AirwallexActivity() {

    private val keyboardController: KeyboardController by lazy {
        KeyboardController(this)
    }

    private val args: AddPaymentMethodActivityStarter.Args by lazy {
        AddPaymentMethodActivityStarter.Args.getExtra(intent)
    }

    private val airwallex: Airwallex by lazy {
        Airwallex()
    }

    private val isValid: Boolean
        get() {
            return cardWidget.isValid && billingWidget.isValid
        }

    override fun onActionSave() {
        val card = cardWidget.paymentMethodCard ?: return
        setLoadingProgress(true)
        airwallex.createPaymentMethod(
            CreatePaymentMethodParams(
                clientSecret = args.clientSecret,
                customerId = args.customerId,
                card = card,
                billing = requireNotNull(billingWidget.billing)
            ),
            object : Airwallex.PaymentListener<PaymentMethod> {
                override fun onSuccess(response: PaymentMethod) {
                    finishWithPaymentMethod(response, card.cvc!!)
                }

                override fun onFailed(exception: AirwallexException) {
                    alertError(exception)
                }
            })
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    private fun alertError(exception: AirwallexException) {
        setLoadingProgress(false)
        alert(message = exception.error?.message ?: exception.toString())
    }

    private fun finishWithPaymentMethod(paymentMethod: PaymentMethod, cvc: String) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK, Intent().putExtras(
                AddPaymentMethodActivityStarter.Result(
                    paymentMethod = paymentMethod,
                    cvc = cvc
                ).toBundle()
            )
        )
        finish()
    }

    private fun invalidateConfirmStatus() {
        if (isValid) {
            tvSaveCard.isEnabled = true
            keyboardController.hide()
        } else {
            tvSaveCard.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cardWidget.cardChangeCallback = { invalidateConfirmStatus() }
        billingWidget.shipping = args.shipping
        billingWidget.billingChangeCallback = { invalidateConfirmStatus() }

        tvSaveCard.isEnabled = isValid
        tvSaveCard.setOnClickListener { onActionSave() }
    }

    override val layoutResource: Int
        get() = R.layout.activity_add_card
}
