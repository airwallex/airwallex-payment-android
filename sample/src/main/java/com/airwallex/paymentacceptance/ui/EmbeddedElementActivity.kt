package com.airwallex.paymentacceptance.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
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
import com.airwallex.android.view.composables.PaymentElement
import com.airwallex.android.view.composables.PaymentElementConfiguration
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.databinding.ActivityEmbeddedElementBinding
import com.airwallex.paymentacceptance.util.PaymentStatusPoller
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * Activity to demonstrate Embedded Element integration
 * Shows order summary, shipping address, and embedded card element
 */
class EmbeddedElementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmbeddedElementBinding

    private val args: Args by lazy {
        intent.getArgs()
    }

    private val session: AirwallexSession by lazy {
        args.session
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    private var loadingDialog: Dialog? = null

    // Payment status poller for handling deep link returns
    private var paymentStatusPoller: PaymentStatusPoller? = null

    // Internal flow for handling payment results
    private val _paymentStatus = MutableSharedFlow<AirwallexPaymentStatus>()
    private val paymentStatus: SharedFlow<AirwallexPaymentStatus> = _paymentStatus.asSharedFlow()

    // Polling result flow
    private val _pollingResult = MutableSharedFlow<PaymentStatusPoller.PollingResult>()
    private val pollingResult: SharedFlow<PaymentStatusPoller.PollingResult> = _pollingResult.asSharedFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmbeddedElementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupColors()
        setupToolbar()
        setupObservers()
        setupCardInfoCompose()
    }

    override fun onDestroy() {
        super.onDestroy()
        paymentStatusPoller?.stop()
        loadingDialog?.dismiss()
    }

    private fun setupColors() {
        binding.root.setBackgroundColor(AirwallexColor.backgroundPrimary().toArgb())

        binding.toolbar.setBackgroundColor(AirwallexColor.backgroundPrimary().toArgb())
        binding.btnBack.setColorFilter(AirwallexColor.iconPrimary().toArgb())

        binding.tvTitle.setTextColor(AirwallexColor.textPrimary().toArgb())

        binding.orderSummaryCard.setBackgroundColor(AirwallexColor.backgroundSecondary().toArgb())
        binding.shippingAddressCard.setBackgroundColor(AirwallexColor.backgroundSecondary().toArgb())

        binding.tvOrderSummaryTitle.setTextColor(AirwallexColor.textPrimary().toArgb())
        binding.tvAirpodsProLabel.setTextColor(AirwallexColor.textPrimary().toArgb())
        binding.tvAirpodsProPrice.setTextColor(AirwallexColor.textPrimary().toArgb())
        binding.tvHomepodLabel.setTextColor(AirwallexColor.textPrimary().toArgb())
        binding.tvHomepodPrice.setTextColor(AirwallexColor.textPrimary().toArgb())
        binding.tvTotalLabel.setTextColor(AirwallexColor.textPrimary().toArgb())
        binding.tvTotalPrice.setTextColor(AirwallexColor.textPrimary().toArgb())
        binding.tvShippingAddressTitle.setTextColor(AirwallexColor.textPrimary().toArgb())
        binding.tvShippingName.setTextColor(AirwallexColor.textPrimary().toArgb())
        binding.tvShippingAddress.setTextColor(AirwallexColor.textSecondary().toArgb())

        // Set divider color
        binding.divider.setBackgroundColor(AirwallexColor.borderDecorative().toArgb())
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        // Observe payment status
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                paymentStatus.collect { status ->
                    handleStatusUpdate(status)
                }
            }
        }

        // Observe polling result
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                pollingResult.collect { result ->
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

            result.onSuccess { manager ->
                // Set up the ComposeView with PaymentElement
                binding.composeCardInfo.setContent {
                    manager.Content()
                }
            }.onFailure { throwable ->
                AirwallexLogger.error("Failed to create PaymentElement", throwable)
                // Handle error - show error message
            }
        }
    }

    private fun handlePaymentResult(status: AirwallexPaymentStatus) {
        lifecycleScope.launch {
            // Emit to flow for processing
            _paymentStatus.emit(status)
        }
    }

    private fun handleStatusUpdate(status: AirwallexPaymentStatus) {
        when (status) {
            is AirwallexPaymentStatus.Success -> {
                Log.d(
                    TAG,
                    "Payment success with intent id: ${status.paymentIntentId}, consent id: ${status.consentId}"
                )
                AirwallexLogger.info("Payment successful: ${status.paymentIntentId}")
                showPaymentSuccess()
            }

            is AirwallexPaymentStatus.InProgress -> {
                // redirecting
                Log.d(TAG, "Payment is redirecting ${status.paymentIntentId}")
                AirwallexLogger.info("Payment in progress: ${status.paymentIntentId}")
//
//                // Start polling
                status.paymentIntentId?.let { intentId ->
                    val clientSecret = session.clientSecret
                    if (!clientSecret.isNullOrEmpty()) {
                        startPolling(intentId, clientSecret)
                    } else {
                        Log.e(TAG, "Client secret is null, cannot start polling")
                    }
                }
            }

            is AirwallexPaymentStatus.Failure -> {
                AirwallexLogger.error("Payment failed", status.exception)
                setLoadingProgress(false)
                showPaymentError(status.exception.localizedMessage)
            }

            is AirwallexPaymentStatus.Cancel -> {
                Log.d(TAG, "User cancel the payment")
                AirwallexLogger.info("Payment cancelled")
                setLoadingProgress(false)
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

    private fun startPolling(intentId: String, clientSecret: String) {
        // Stop any existing poller first
        paymentStatusPoller?.stop()

        val poller = PaymentStatusPoller(
            intentId = intentId,
            clientSecret = clientSecret,
            airwallex = airwallex
        )
        paymentStatusPoller = poller

        lifecycleScope.launch {
            setLoadingProgress(true, cancellable = true)
            val result = poller.getPaymentAttempt()
            _pollingResult.emit(result)
            paymentStatusPoller = null
            setLoadingProgress(false)
        }
    }

    // UI Helper Methods
    private fun setLoadingProgress(loading: Boolean, cancellable: Boolean = false) {
        if (loading) {
            startWait(cancellable)
        } else {
            endWait()
        }
    }

    private fun startWait(cancellable: Boolean = false) {
        // If dialog already showing, just update cancelable state (reentrant)
        if (loadingDialog?.isShowing == true) {
            loadingDialog?.setCancelable(cancellable)
            if (cancellable) {
                loadingDialog?.setOnCancelListener { onLoadingCancelled() }
            } else {
                loadingDialog?.setOnCancelListener(null)
            }
            return
        }
        if (!isFinishing) {
            try {
                loadingDialog = Dialog(this).apply {
                    setContentView(R.layout.airwallex_loading)
                    val progressBar = findViewById<ProgressBar>(R.id.airwallex_progress_bar)
                    progressBar.indeterminateTintList = ColorStateList.valueOf(
                        AirwallexColor.theme().toArgb()
                    )
                    window?.setBackgroundDrawableResource(android.R.color.transparent)
                    setCancelable(cancellable)
                    if (cancellable) {
                        setOnCancelListener { onLoadingCancelled() }
                    }
                    show()
                }
            } catch (e: Exception) {
                Log.d(TAG, "Failed to show loading dialog", e)
            }
        } else {
            loadingDialog = null
        }
    }

    private fun onLoadingCancelled() {
        paymentStatusPoller?.stop()
    }

    private fun endWait() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun showPaymentSuccess() {
        showAlert(
            getString(R.string.payment_successful),
            getString(R.string.payment_successful_message)
        ) {
            finish()
        }
    }

    private fun showPaymentError(error: String? = null) {
        showAlert(
            getString(R.string.payment_failed),
            error ?: getString(R.string.payment_failed_message)
        )
    }

    private fun showPaymentCancelled(error: String? = null) {
        showAlert(
            getString(R.string.payment_cancelled),
            error ?: getString(R.string.payment_cancelled_message)
        )
    }

    private fun showAlert(title: String, message: String, callback: (() -> Unit)? = null) {
        if (!isFinishing) {
            val dialog = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    callback?.invoke()
                }
                .create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                ColorStateList.valueOf(AirwallexColor.theme().toArgb())
            )
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
        private const val TAG = "EmbeddedElementActivity"
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
