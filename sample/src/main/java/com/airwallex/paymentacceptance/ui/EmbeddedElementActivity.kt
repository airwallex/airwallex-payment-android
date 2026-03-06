package com.airwallex.paymentacceptance.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.view.composables.PaymentElementConfiguration
import com.airwallex.android.view.composables.PaymentElement
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.databinding.ActivityEmbeddedElementBinding
import com.airwallex.paymentacceptance.ui.base.BasePaymentActivity
import com.airwallex.paymentacceptance.util.PaymentStatusPoller
import com.airwallex.paymentacceptance.viewmodel.EmbeddedElementViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * Activity to demonstrate Embedded Element integration
 * Shows order summary, shipping address, and embedded card element
 */
class EmbeddedElementActivity : BasePaymentActivity<ActivityEmbeddedElementBinding, EmbeddedElementViewModel>() {

    private val args: Args by lazy {
        intent.getArgs()
    }

    private val session: AirwallexSession by lazy {
        args.session
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    override fun getViewBinding(): ActivityEmbeddedElementBinding {
        return ActivityEmbeddedElementBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<EmbeddedElementViewModel> {
        return EmbeddedElementViewModel::class.java
    }

    override fun initView() {
        setupColors()
        setupCardInfoCompose()
    }

    override fun initListener() {
        setupToolbar()
    }

    override fun addObserver() {
        setupObservers()
    }

    private fun setupColors() {
        mBinding.root.setBackgroundColor(AirwallexColor.backgroundPrimary.toArgb())

        mBinding.toolbar.setBackgroundColor(AirwallexColor.backgroundPrimary.toArgb())
        mBinding.btnBack.setColorFilter(AirwallexColor.iconPrimary.toArgb())

        mBinding.tvTitle.setTextColor(AirwallexColor.textPrimary.toArgb())

        mBinding.orderSummaryCard.setBackgroundColor(AirwallexColor.backgroundSecondary.toArgb())
        mBinding.shippingAddressCard.setBackgroundColor(AirwallexColor.backgroundSecondary.toArgb())

        mBinding.tvOrderSummaryTitle.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvAirpodsProLabel.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvAirpodsProPrice.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvHomepodLabel.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvHomepodPrice.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvTotalLabel.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvTotalPrice.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvShippingAddressTitle.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvShippingName.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvShippingAddress.setTextColor(AirwallexColor.textSecondary.toArgb())

        // Set divider color
        mBinding.divider.setBackgroundColor(AirwallexColor.borderDecorative.toArgb())
    }

    private fun setupToolbar() {
        mBinding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        // Observe payment status from ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mViewModel.airwallexPaymentStatus.collect { status ->
                    handleStatusUpdate(status)
                }
            }
        }

        // Observe polling result from ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mViewModel.pollingResult.collect { result ->
                    handlePollingResult(result)
                }
            }
        }
    }

    private fun setupCardInfoCompose() {
        lifecycleScope.launch {
            // Determine configuration based on layoutType
            val configuration = when (args.layoutType) {
                PaymentMethodsLayoutType.TAB -> PaymentElementConfiguration.PaymentSheet(
                    layout = PaymentMethodsLayoutType.TAB
                )
                PaymentMethodsLayoutType.ACCORDION -> PaymentElementConfiguration.PaymentSheet(
                    layout = PaymentMethodsLayoutType.ACCORDION
                )
                null -> PaymentElementConfiguration.Card(
                    supportedCardBrands = args.supportedCardSchemes ?: emptyList()
                )
            }

            val result = PaymentElement.create(
                session = session,
                airwallex = airwallex,
                configuration = configuration,
                onLoadingStateChanged = { isLoading ->
                    setLoadingProgress(loading = isLoading, cancellable = true)
                },
                onPaymentResult = { status ->
                    handlePaymentResult(status)
                },
                onError = { throwable ->
                    AirwallexLogger.error("Create element failed", throwable)
                    showAlert(
                        getString(R.string.payment_failed),
                        throwable.message ?: throwable.toString()
                    )
                }
            )

            result.onSuccess {
                // Set up the ComposeView with PaymentElement
                mBinding.composeCardInfo.setContent {
                    it.Content()
                }
            }.onFailure { throwable ->
                AirwallexLogger.error("Failed to create PaymentElement", throwable)
                // Handle error - show error message
            }
        }
    }

    private fun handlePaymentResult(status: AirwallexPaymentStatus) {
        // Delegate to ViewModel which handles polling if needed
        mViewModel.handlePaymentResult(session, status)
    }

    private fun handleStatusUpdate(status: AirwallexPaymentStatus) {
        when (status) {
            is AirwallexPaymentStatus.Success -> {
                AirwallexLogger.info("Payment successful: ${status.paymentIntentId}")
                showPaymentSuccess { finish() }
            }

            is AirwallexPaymentStatus.InProgress -> {
                // ViewModel handles polling automatically
                setLoadingProgress(loading = true, cancellable = true)
                AirwallexLogger.info("Payment in progress: ${status.paymentIntentId}")
            }

            is AirwallexPaymentStatus.Failure -> {
                AirwallexLogger.error("Payment failed", status.exception)
                showPaymentError(status.exception.localizedMessage)
            }

            is AirwallexPaymentStatus.Cancel -> {
                AirwallexLogger.info("Payment cancelled")
                showPaymentCancelled()
            }
        }
    }

    private fun handlePollingResult(result: PaymentStatusPoller.PollingResult) {
        when (result) {
            is PaymentStatusPoller.PollingResult.Complete -> {
                showAlert("Payment Result", result.description) {
                    finish()
                }
            }
            is PaymentStatusPoller.PollingResult.Timeout -> {
                showAlert("Polling Timeout", result.description)
            }
            is PaymentStatusPoller.PollingResult.Error -> {
                showPaymentError(result.message)
            }
            is PaymentStatusPoller.PollingResult.PaymentAttemptNotFound -> {
                showPaymentError("Payment attempt not found")
            }
        }
    }

    @Suppress("ParcelCreator")
    @Parcelize
    data class Args(
        val session: AirwallexSession,
        val layoutType: PaymentMethodsLayoutType? = null,
        val supportedCardSchemes: List<CardScheme>? = null
    ) : Parcelable

    companion object {
        private const val EXTRA_BUNDLE = "extra_bundle"
        private const val EXTRA_ARGS = "extra_args"

        fun start(
            context: Context,
            session: AirwallexSession,
            layoutType: PaymentMethodsLayoutType? = null,
            supportedCardSchemes: List<CardScheme>? = null
        ) {
            val args = Args(
                session = session,
                layoutType = layoutType,
                supportedCardSchemes = supportedCardSchemes
            )
            val intent = Intent(context, EmbeddedElementActivity::class.java).apply {
                putExtra(EXTRA_BUNDLE, Bundle().apply {
                    putParcelable(EXTRA_ARGS, args)
                })
            }
            context.startActivity(intent)
            (context as? Activity)?.overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }

        private fun Intent.getArgs(): Args {
            return getBundleExtra(EXTRA_BUNDLE).let {
                requireNotNull(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it?.getParcelable(EXTRA_ARGS, Args::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        it?.getParcelable(EXTRA_ARGS)
                    }
                )
            }
        }
    }
}
