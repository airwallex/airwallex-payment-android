package com.airwallex.android.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.R
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.exception.ThreeDSCancelledException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.databinding.ActivityPaymentMethodsBinding
import com.airwallex.android.ui.checkout.AirwallexCheckoutBaseActivity
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.ui.extension.getExtraArgs
import com.airwallex.android.view.composables.PaymentElementConfiguration
import com.airwallex.android.view.composables.PaymentElement
import com.airwallex.android.view.composables.PaymentScreen
import com.airwallex.android.view.composables.PaymentScreenLoadingState
import com.airwallex.risk.AirwallexRisk
import kotlinx.coroutines.launch
import androidx.core.graphics.drawable.toDrawable
import com.airwallex.android.core.log.AnalyticsLogger

@Suppress("LongMethod")
class PaymentMethodsActivity : AirwallexCheckoutBaseActivity(), TrackablePage {

    private val viewBinding: ActivityPaymentMethodsBinding by lazy {
        viewStub.layoutResource = R.layout.activity_payment_methods
        val root = viewStub.inflate() as ViewGroup
        ActivityPaymentMethodsBinding.bind(root)
    }

    private val args: PaymentMethodsActivityLaunch.Args by lazy {
        intent.getExtraArgs()
    }

    override val session: AirwallexSession by lazy {
        args.session
    }

    override val pageName: String
        get() = "payment_method_list"

    override val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    override fun initView() {
        super.initView()
        viewBinding.root.setBackgroundColor(AirwallexColor.backgroundPrimary.toArgb())
        supportActionBar?.let { actionBar ->
            actionBar.setBackgroundDrawable(
                AirwallexColor.backgroundPrimary.toArgb().toDrawable()
            )
        }
        supportActionBar?.themedContext?.let { context ->
            ContextCompat.getDrawable(context, homeAsUpIndicatorResId())?.let { drawable ->
                val tintedDrawable = DrawableCompat.wrap(drawable.mutate())
                DrawableCompat.setTint(tintedDrawable, AirwallexColor.iconPrimary.toArgb())
                supportActionBar?.setHomeAsUpIndicator(tintedDrawable)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentScreenLoadingState.isLoading = true

        lifecycleScope.launch {
            PaymentElement.create(
                session = session,
                airwallex = airwallex,
                configuration = PaymentElementConfiguration.PaymentSheet(layout = args.layoutType, showsGooglePayAsPrimaryButton = args.showsGooglePayAsPrimaryButton),
                launchType = AnalyticsLogger.LaunchType.HPP,
                paymentFlowListener = object : PaymentFlowListener {
                    override fun onLoadingStateChanged(isLoading: Boolean, context: Context) {
                        setLoadingProgress(loading = isLoading, cancelable = false)
                    }

                    override fun onPaymentResult(status: AirwallexPaymentStatus) {
                        handlePaymentStatus(status)
                    }
                }
            ).fold(
                onSuccess = { state ->
                    PaymentScreenLoadingState.isLoading = false
                    initView(state)
                },
                onFailure = { error ->
                    setLoadingProgress(loading = false, cancelable = false)
                    alert(message = error.message ?: error.toString())
                }
            )
        }
    }

    override fun onBackButtonPressed() {
        super.onBackButtonPressed()
        AirwallexLogger.info("PaymentMethodsActivity onBackButtonPressed")
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun addObserver() {
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_close
    }

    private fun initView(paymentElementState: PaymentElement) {
        AirwallexRisk.log(event = "show_payment_method_list", screen = "page_payment_method_list")
        viewBinding.composeView.apply {
            setContent {
                AirwallexTheme {
                    PaymentScreen(
                        paymentElementState = paymentElementState
                    )
                }
            }
        }
    }

    private fun handlePaymentStatus(status: AirwallexPaymentStatus) {
        when (status) {
            is AirwallexPaymentStatus.Success -> {
                finishWithPaymentIntent(
                    paymentIntentId = status.paymentIntentId,
                    isRedirecting = false,
                )
            }

            is AirwallexPaymentStatus.Failure -> {
                // Check if it's a 3DS cancellation - stay open to allow retry
                if (status.exception is ThreeDSCancelledException) {
                    setLoadingProgress(false)
                } else {
                    finishWithPaymentIntent(exception = status.exception)
                }
            }

            is AirwallexPaymentStatus.InProgress -> {
                finishWithPaymentIntent(
                    paymentIntentId = status.paymentIntentId,
                    isRedirecting = true,
                )
            }

            is AirwallexPaymentStatus.Cancel -> {
                setLoadingProgress(false)
            }
        }
    }

    private fun finishWithPaymentIntent(
        paymentIntentId: String? = null,
        isRedirecting: Boolean = false,
        exception: AirwallexException? = null,
        consentId: String? = null,
    ) {
        setLoadingProgress(false)
        AirwallexLogger.info("PaymentMethodsActivity finishWithPaymentIntent")
        setResult(
            RESULT_OK,
            Intent().putExtras(
                PaymentMethodsActivityLaunch.Result(
                    paymentIntentId = paymentIntentId,
                    paymentConsentId = consentId,
                    isRedirecting = isRedirecting,
                    exception = exception
                ).toBundle()
            ),
        )
        finish()
    }
}