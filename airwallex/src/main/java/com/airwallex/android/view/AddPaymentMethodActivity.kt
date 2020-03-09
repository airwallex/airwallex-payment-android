package com.airwallex.android.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.airwallex.android.Airwallex
import com.airwallex.android.R
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodParams
import com.airwallex.android.model.PaymentMethodType
import java.util.*
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.activity_airwallex.*

internal class AddPaymentMethodActivity : AirwallexActivity() {

    private val args: AddPaymentMethodActivityStarter.Args by lazy {
        AddPaymentMethodActivityStarter.Args.getExtra(intent)
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(
            requireNotNull(args.token),
            requireNotNull(args.paymentIntent.clientSecret)
        )
    }

    override fun onActionSave() {
        val card = cardWidget.paymentMethodCard ?: return
        setLoadingProgress(true)
        val paymentMethodParams = PaymentMethodParams.Builder()
            .setCustomerId(args.paymentIntent.customerId)
            .setRequestId(UUID.randomUUID().toString())
            .setType(PaymentMethodType.CARD.type)
            .setCard(card)
            .setBilling(billingWidget.billing)
            .build()

        airwallex.createPaymentMethod(
            paymentMethodParams,
            object : Airwallex.PaymentCallback<PaymentMethod> {
                override fun onSuccess(response: PaymentMethod) {
                    onActionSave(response, card.cvc!!)
                }

                override fun onFailed(exception: AirwallexException) {
                    setLoadingProgress(false)
                    alert(message = exception.error?.message ?: exception.toString())
                }
            })
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    private fun onActionSave(paymentMethod: PaymentMethod, cvc: String) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK, Intent()
                .putExtras(AddPaymentMethodActivityStarter.Result(paymentMethod, cvc).toBundle())
        )
        finish()
    }

    private fun isValid(): Boolean {
        return cardWidget.isValid && billingWidget.isValid
    }

    private fun invalidateConfirmStatus() {
        val isValid = isValid()
        tvSaveCard.isEnabled = isValid()
        if (isValid) {
            hideKeyboard(this)
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_add_card
        viewStub.inflate()

        cardWidget.cardChangeCallback = { invalidateConfirmStatus() }
        billingWidget.shipping = args.paymentIntent.order.shipping
        billingWidget.billingChangeCallback = { invalidateConfirmStatus() }

        tvSaveCard.isEnabled = isValid()
        tvSaveCard.setOnClickListener { onActionSave() }
    }
}
