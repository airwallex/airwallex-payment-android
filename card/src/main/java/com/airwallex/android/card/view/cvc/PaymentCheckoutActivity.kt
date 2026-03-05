package com.airwallex.android.card.view.cvc

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toDrawable
import com.airwallex.android.card.R
import com.airwallex.android.card.databinding.ActivityPaymentCheckoutBinding
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.WeChat
import com.airwallex.android.core.util.CurrencyUtils.formatPrice
import com.airwallex.android.ui.checkout.AirwallexCheckoutBaseActivity
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.extension.getExtraArgs
import java.util.*

/**
 * Activity to confirm payment intent
 */
class PaymentCheckoutActivity : AirwallexCheckoutBaseActivity() {

    private val viewBinding: ActivityPaymentCheckoutBinding by lazy {
        viewStub.layoutResource = R.layout.activity_payment_checkout
        val root = viewStub.inflate() as ViewGroup
        ActivityPaymentCheckoutBinding.bind(root)
    }

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    private val args: PaymentCheckoutActivityLaunch.Args by lazy {
        intent.getExtraArgs()
    }

    private val paymentMethod: PaymentMethod by lazy {
        args.paymentMethod
    }

    private val paymentConsentId: String? by lazy {
        args.paymentConsentId
    }

    override val session: AirwallexSession by lazy {
        args.session
    }

    override fun onBackButtonPressed() {
        super.onBackButtonPressed()
        AirwallexLogger.info("PaymentCheckoutActivity onBackButtonPressed")
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }

    override fun initView() {
        super.initView()

        supportActionBar?.let { actionBar ->
            actionBar.setBackgroundDrawable(
                AirwallexColor.backgroundPrimary.toArgb().toDrawable()
            )
            val upArrow = AppCompatResources.getDrawable(this, homeAsUpIndicatorResId())
            upArrow?.let {
                val wrappedDrawable = DrawableCompat.wrap(it)
                DrawableCompat.setTint(wrappedDrawable, AirwallexColor.iconPrimary.toArgb())
                actionBar.setHomeAsUpIndicator(wrappedDrawable)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding.root.setBackgroundColor(AirwallexColor.backgroundPrimary.toArgb())
        viewBinding.header.text = getString(
            R.string.airwallex_card_enter_cvv,
            String.format(
                "%s •••• %s",
                paymentMethod.card?.brand?.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(
                            Locale.getDefault()
                        )
                    } else it.toString()
                },
                paymentMethod.card?.last4
            )
        )
        viewBinding.header.setTextColor(AirwallexColor.textPrimary.toArgb())

        viewBinding.atlCardCvc.setCardNumber(paymentMethod.card?.number)
        viewBinding.tvTotalPrice.text =
            getString(R.string.airwallex_card_total, formatPrice(session.currency, session.amount))
        viewBinding.tvTotalPrice.setTextColor(AirwallexColor.textSecondary.toArgb())

        setupButtonColors()

        viewBinding.atlCardCvc.afterTextChanged {
            updateButtonStatus()
        }

        viewBinding.rlPayNow.setOnSingleClickListener {
            startConfirmPaymentIntent()
        }
        updateButtonStatus()
    }

    private fun setupButtonColors() {
        val textColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf()
            ),
            intArrayOf(
                AirwallexColor.textSecondary.toArgb(),
                AirwallexColor.textInverse.toArgb()
            )
        )
        viewBinding.rlPayNow.setTextColor(textColorStateList)

        val backgroundDrawable = StateListDrawable()

        val disabledDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(AirwallexColor.borderDecorative.toArgb())
            cornerRadius = 6f * resources.displayMetrics.density
        }
        backgroundDrawable.addState(intArrayOf(-android.R.attr.state_enabled), disabledDrawable)

        val enabledDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(AirwallexColor.theme.toArgb())
            cornerRadius = 6f * resources.displayMetrics.density
        }
        backgroundDrawable.addState(intArrayOf(), enabledDrawable)

        viewBinding.rlPayNow.background = backgroundDrawable
    }

    private fun startConfirmPaymentIntent() {
        AirwallexLogger.info("PaymentCheckoutActivity startConfirmPaymentIntent")
        startCheckout(
            paymentMethod = paymentMethod,
            paymentConsentId = paymentConsentId,
            cvc = viewBinding.atlCardCvc.value,
            observer = { result ->
                when (result) {
                    is AirwallexPaymentStatus.Success -> {
                        AirwallexLogger.info("PaymentCheckoutActivity startConfirmPaymentIntent success")
                        finishWithPaymentIntent(paymentIntentId = result.paymentIntentId)
                    }
                    is AirwallexPaymentStatus.Failure -> {
                        AirwallexLogger.error("PaymentCheckoutActivity startConfirmPaymentIntent fail", result.exception)
                        finishWithPaymentIntent(exception = result.exception)
                    }
                    else -> Unit
                }
            }
        )
    }

    private fun finishWithPaymentIntent(
        paymentIntentId: String? = null,
        weChat: WeChat? = null,
        redirectUrl: String? = null,
        exception: AirwallexException? = null
    ) {
        setLoadingProgress(false)
        AirwallexLogger.info("PaymentCheckoutActivity finishWithPaymentIntent")
        setResult(
            RESULT_OK,
            Intent().putExtras(
                PaymentCheckoutActivityLaunch.Result(
                    paymentIntentId = paymentIntentId,
                    weChat = weChat,
                    redirectUrl = redirectUrl,
                    exception = exception
                ).toBundle()
            )
        )
        finish()
    }

    private fun updateButtonStatus() {
        viewBinding.rlPayNow.isEnabled = viewBinding.atlCardCvc.isValid
    }
}
