package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.*
import com.airwallex.android.ClientSecretRepository
import com.airwallex.android.model.CreatePaymentMethodParams
import com.airwallex.android.model.ClientSecret
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.Shipping
import android.view.ViewGroup
import com.airwallex.android.Airwallex
import com.airwallex.android.R
import com.airwallex.android.databinding.ActivityAddCardBinding

/**
 * Activity to add new payment method
 */
internal class AddPaymentMethodActivity : AirwallexActivity() {

    private val viewBinding: ActivityAddCardBinding by lazy {
        viewStub.layoutResource = R.layout.activity_add_card
        val root = viewStub.inflate() as ViewGroup
        ActivityAddCardBinding.bind(root)
    }

    private val keyboardController: KeyboardController by lazy {
        KeyboardController(this)
    }

    private val args: AddPaymentMethodActivityLaunch.Args by lazy {
        AddPaymentMethodActivityLaunch.Args.getExtra(intent)
    }

    private val session: AirwallexSession by lazy {
        args.session
    }

    private val customerId: String by lazy {
        requireNotNull(session.customerId)
    }

    private val shipping: Shipping? by lazy {
        when (session) {
            is AirwallexPaymentSession -> {
                (session as AirwallexPaymentSession).paymentIntent.order?.shipping
            }
            is AirwallexRecurringWithIntentSession -> {
                (session as AirwallexRecurringWithIntentSession).paymentIntent.order?.shipping
            }
            is AirwallexRecurringSession -> {
                (session as AirwallexRecurringSession).shipping
            }
            else -> null
        }
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    private val isValid: Boolean
        get() {
            return viewBinding.cardWidget.isValid && viewBinding.billingWidget.isValid
        }

    override fun onActionSave() {
        val card = viewBinding.cardWidget.paymentMethodCard ?: return
        setLoadingProgress(loading = true, cancelable = false)
        ClientSecretRepository.getInstance().retrieveClientSecret(
            customerId,
            object : ClientSecretRepository.ClientSecretRetrieveListener {
                override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                    airwallex.createPaymentMethod(
                        CreatePaymentMethodParams(
                            clientSecret = clientSecret.value,
                            customerId = customerId,
                            card = card,
                            billing = viewBinding.billingWidget.billing
                        ),
                        object : Airwallex.PaymentListener<PaymentMethod> {
                            override fun onSuccess(response: PaymentMethod) {
                                finishWithPaymentMethod(response, requireNotNull(card.cvc))
                            }

                            override fun onFailed(exception: Exception) {
                                alertError(exception.message ?: exception.toString())
                            }
                        }
                    )
                }

                override fun onClientSecretError(errorMessage: String) {
                    alertError(errorMessage)
                }
            }
        )
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    private fun alertError(error: String) {
        setLoadingProgress(false)
        alert(message = error)
    }

    private fun finishWithPaymentMethod(paymentMethod: PaymentMethod, cvc: String) {
        setLoadingProgress(false)
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                AddPaymentMethodActivityLaunch.Result(
                    paymentMethod = paymentMethod,
                    cvc = cvc
                ).toBundle()
            )
        )
        finish()
    }

    private fun invalidateConfirmStatus() {
        if (isValid) {
            viewBinding.tvSaveCard.isEnabled = true
            keyboardController.hide()
        } else {
            viewBinding.tvSaveCard.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.cardWidget.cardChangeCallback = { invalidateConfirmStatus() }
        viewBinding.billingWidget.shipping = shipping
        viewBinding.billingWidget.billingChangeCallback = { invalidateConfirmStatus() }

        viewBinding.tvSaveCard.isEnabled = isValid
        viewBinding.tvSaveCard.setOnClickListener { onActionSave() }
    }
}
