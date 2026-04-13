package com.airwallex.paymentacceptance.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.AirwallexSupportedCard
import com.airwallex.android.core.exception.ThreeDSCancelledException
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.view.composables.PaymentElementConfiguration
import com.airwallex.android.view.composables.PaymentElement
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.databinding.ActivityEmbeddedElementBinding
import com.airwallex.paymentacceptance.ui.base.BasePaymentActivity
import com.airwallex.paymentacceptance.util.PaymentStatusPoller
import com.airwallex.paymentacceptance.viewmodel.EmbeddedElementViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency

/**
 * Activity to demonstrate Embedded Element integration
 * Shows order summary, shipping address, and embedded card element
 */
class EmbeddedElementActivity :
    BasePaymentActivity<ActivityEmbeddedElementBinding, EmbeddedElementViewModel>() {

    private val args: Args by lazy {
        intent.getArgs()
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
        updatePrices()
        setupCardInfoCompose()
    }

    override fun initListener() {
        setupToolbar()
    }

    override fun addObserver() {
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        // Start polling when activity resumes (e.g., returning from redirect)
        mViewModel.startPendingPollingIfNeeded()
    }

    private fun setupColors() {
        mBinding.root.setBackgroundColor(AirwallexColor.backgroundPrimary.toArgb())
        mBinding.toolbar.setBackgroundColor(AirwallexColor.backgroundPrimary.toArgb())
        mBinding.btnBack.setColorFilter(AirwallexColor.iconPrimary.toArgb())
        mBinding.tvTitle.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvPaymentMethodsTitle.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.orderDetailsCard.setCardBackgroundColor(AirwallexColor.backgroundSecondary.toArgb())

        mBinding.tvMerchantName.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvOrderDetailsLabel.setTextColor(AirwallexColor.textPrimary.toArgb())

        mBinding.tvOrderSummaryTitle.setTextColor(AirwallexColor.textPrimary.toArgb())

        mBinding.tvProduct1Name.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvProduct1Detail.setTextColor(AirwallexColor.textSecondary.toArgb())
        mBinding.tvProduct1Price.setTextColor(AirwallexColor.textPrimary.toArgb())

        mBinding.tvProduct2Name.setTextColor(AirwallexColor.textPrimary.toArgb())
        mBinding.tvProduct2Detail.setTextColor(AirwallexColor.textSecondary.toArgb())
        mBinding.tvProduct2Price.setTextColor(AirwallexColor.textPrimary.toArgb())

        mBinding.tvFeeLabel.setTextColor(AirwallexColor.textSecondary.toArgb())
        mBinding.tvFeeAmount.setTextColor(AirwallexColor.textSecondary.toArgb())
        mBinding.tvSubtotalLabel.setTextColor(AirwallexColor.textSecondary.toArgb())
        mBinding.tvSubtotalAmount.setTextColor(AirwallexColor.textSecondary.toArgb())

        mBinding.tvTotalLabel.setTextColor(AirwallexColor.textSecondary.toArgb())
        mBinding.tvTotalPrice.setTextColor(AirwallexColor.textPrimary.toArgb())

        mBinding.progressBar.indeterminateTintList = ColorStateList.valueOf(
            AirwallexColor.theme.toArgb()
        )
    }

    private fun updatePrices() {
        val productSum = PRODUCT_1_PRICE + PRODUCT_2_PRICE
        val finalAmount = Settings.price.toBigDecimalOrNull() ?: BigDecimal(productSum)
        val difference = finalAmount.subtract(BigDecimal(productSum))

        val currencyCode = Settings.currency
        val numberFormat = NumberFormat.getCurrencyInstance().apply {
            try {
                currency = Currency.getInstance(currencyCode)
            } catch (e: IllegalArgumentException) {
                // Fallback to USD if currency code is invalid
                currency = Currency.getInstance("USD")
            }
        }

        // Update product prices (static)
        mBinding.tvProduct1Price.text = numberFormat.format(PRODUCT_1_PRICE)
        mBinding.tvProduct2Price.text = numberFormat.format(PRODUCT_2_PRICE)

        // Update fee/discount based on difference
        when {
            difference > BigDecimal.ZERO -> {
                mBinding.tvFeeLabel.text = "Fee"
                mBinding.tvFeeAmount.text = numberFormat.format(difference)
                mBinding.tvFeeLabel.visibility = View.VISIBLE
                mBinding.tvFeeAmount.visibility = View.VISIBLE
            }
            difference < BigDecimal.ZERO -> {
                mBinding.tvFeeLabel.text = "Discount"
                mBinding.tvFeeAmount.text = numberFormat.format(difference.abs())
                mBinding.tvFeeLabel.visibility = View.VISIBLE
                mBinding.tvFeeAmount.visibility = View.VISIBLE
            }
            else -> {
                mBinding.tvFeeLabel.visibility = View.GONE
                mBinding.tvFeeAmount.visibility = View.GONE
            }
        }

        // Update subtotal and total
        mBinding.tvSubtotalAmount.text = numberFormat.format(finalAmount)
        mBinding.tvTotalPrice.text = "${finalAmount.toPlainString()} $currencyCode"
    }

    private fun setupToolbar() {
        mBinding.btnBack.setOnClickListener {
            finish()
        }

        // Setup expand/collapse for order details
        var isExpanded = false
        mBinding.orderDetailsHeader.setOnClickListener {
            isExpanded = !isExpanded
            mBinding.orderDetailsContent.visibility = if (isExpanded) {
                View.VISIBLE
            } else {
                View.GONE
            }

            // Rotate chevron
            mBinding.ivChevron.animate()
                .rotation(if (isExpanded) 180f else 0f)
                .setDuration(200)
                .start()
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
        mBinding.progressBar.visibility = View.VISIBLE
        mBinding.composeCardInfo.visibility = View.GONE

        // Create session first
        val includeGooglePay = args.layoutType != null // Include Google Pay for tab/accordion layouts
        mViewModel.createEmbeddedElementSession(
            includeGooglePay = includeGooglePay,
            paymentMethods = args.paymentMethods,
            onSuccess = {
                setupPaymentElement()
            },
            onError = { e ->
                AirwallexLogger.error("Failed to create session", e)
                mBinding.progressBar.visibility = View.GONE
                showAlert(
                    getString(R.string.payment_failed),
                    e.message ?: e.toString()
                )
            }
        )
    }

    private fun setupPaymentElement() {
        lifecycleScope.launch {
            val session = mViewModel.getSession() ?: run {
                AirwallexLogger.error("Session is null")
                mBinding.progressBar.visibility = View.GONE
                showAlert(
                    getString(R.string.payment_failed),
                    "Session not created"
                )
                return@launch
            }

            // Determine configuration based on layoutType
            val configuration = when (args.layoutType) {
                PaymentMethodsLayoutType.TAB -> PaymentElementConfiguration.PaymentSheet(
                    layout = PaymentMethodsLayoutType.TAB,
                    googlePayButton = PaymentElementConfiguration.GooglePayButton(
                        showsAsPrimaryButton = args.showsGooglePayAsPrimaryButton
                    )
                )

                PaymentMethodsLayoutType.ACCORDION -> PaymentElementConfiguration.PaymentSheet(
                    layout = PaymentMethodsLayoutType.ACCORDION,
                    googlePayButton = PaymentElementConfiguration.GooglePayButton(
                        showsAsPrimaryButton = args.showsGooglePayAsPrimaryButton
                    )
                )

                null -> PaymentElementConfiguration.Card(
                    supportedCardBrands = args.supportedCardBrands ?: enumValues<AirwallexSupportedCard>().toList()
                )
            }

            val result = PaymentElement.create(
                session = session,
                airwallex = airwallex,
                configuration = configuration,
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
                //loading state end
                mBinding.progressBar.visibility = View.GONE
                mBinding.composeCardInfo.visibility = View.VISIBLE
                mBinding.composeCardInfo.setContent {
                    it.Content()
                }
            }.onFailure { throwable ->
                AirwallexLogger.error("Failed to create PaymentElement", throwable)
                mBinding.progressBar.visibility = View.GONE
                // Handle error - show error message
            }
        }
    }

    private fun handlePaymentResult(status: AirwallexPaymentStatus) {
        // Delegate to ViewModel which handles polling if needed
        mViewModel.handlePaymentResult(status)
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
                if (status.exception is ThreeDSCancelledException) {
                    showPaymentCancelled()
                } else {
                    showPaymentError(status.exception.localizedMessage)
                }
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
        val layoutType: PaymentMethodsLayoutType? = null,
        val supportedCardBrands: List<AirwallexSupportedCard>? = null,
        val showsGooglePayAsPrimaryButton: Boolean = true,
        val paymentMethods: List<String> = listOf()
    ) : Parcelable

    companion object {
        private const val EXTRA_BUNDLE = "extra_bundle"
        private const val EXTRA_ARGS = "extra_args"
        private const val PRODUCT_1_PRICE = 300.00
        private const val PRODUCT_2_PRICE = 100.00

        fun start(
            context: Context,
            layoutType: PaymentMethodsLayoutType? = null,
            supportedCardBrands: List<AirwallexSupportedCard>? = null,
            showsGooglePayAsPrimaryButton: Boolean = true,
            paymentMethods: List<String> = listOf()
        ) {
            val args = Args(
                layoutType = layoutType,
                supportedCardBrands = supportedCardBrands,
                showsGooglePayAsPrimaryButton = showsGooglePayAsPrimaryButton,
                paymentMethods = paymentMethods
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
