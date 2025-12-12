package com.airwallex.paymentacceptance.ui

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.ui.base.BasePaymentTypeActivity
import com.airwallex.paymentacceptance.ui.bean.ButtonItem
import com.airwallex.paymentacceptance.util.PaymentStatusPoller
import com.airwallex.paymentacceptance.viewmodel.UIIntegrationViewModel
import kotlinx.coroutines.launch

/**
 * This Activity demonstrates how to call the payment flow UI provided by Airwallex.
 */
class UIIntegrationActivity :
    BasePaymentTypeActivity<UIIntegrationViewModel>() {

    override fun initView() {
        super.initView()
        mBinding.titleView.setTitle("Integrate with Airwallex UI")
    }

    override fun addObserver() {
        super.addObserver()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mViewModel.airwallexPaymentStatus.collect { status ->
                    handleStatusUpdate(status)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mViewModel.pollingResult.collect { result ->
                    handlePollingResult(result)
                }
            }
        }
        mViewModel.isLoading.observe(this) { isLoading ->
            setLoadingProgress(isLoading)
        }
        mViewModel.airwallexShippingStatus.observe(this) {

        }
    }

    override fun getButtonList(): List<ButtonItem> {
        val allButtons = listOf(
            ButtonItem(LAUNCH_PAYMENT_LIST, "Launch payment list"),
            ButtonItem(LAUNCH_CUSTOM_PAYMENT_LIST, "Launch custom payment list"),
            ButtonItem(LAUNCH_CARD_PAYMENT, "Launch card payment"),
            ButtonItem(LAUNCH_CARD_PAYMENT_DIALOG, "Launch card payment (dialog)"),
            ButtonItem(LAUNCH_SHIPPING_ADDRESS_DIALOG, "Launch shipping address (dialog)")
        )

        return if (Settings.expressCheckout == "Enabled") {
            // Hide "Launch payment list" and "Launch custom payment list" when Express Checkout is enabled
            allButtons.filter {
                it.id != LAUNCH_PAYMENT_LIST && it.id != LAUNCH_CUSTOM_PAYMENT_LIST
            }
        } else {
            allButtons
        }
    }

    override fun handleBtnClick(id: Int) {
        when (id) {
            LAUNCH_PAYMENT_LIST -> {
                mViewModel.launchPaymentList(this)
            }

            LAUNCH_CUSTOM_PAYMENT_LIST -> {
                mViewModel.launchCustomPaymentList(this)
            }

            LAUNCH_CARD_PAYMENT -> {
                mViewModel.launchCardPage(this)
            }

            LAUNCH_CARD_PAYMENT_DIALOG -> {
                mViewModel.launchCardDialog(this)
            }

            LAUNCH_SHIPPING_ADDRESS_DIALOG -> {
                mViewModel.launchShipping(this)
            }
        }
    }


    private fun handleStatusUpdate(status: AirwallexPaymentStatus) {
        when (status) {
            is AirwallexPaymentStatus.Success -> {
                Log.d(
                    TAG,
                    "Payment success with intent id: ${status.paymentIntentId}, consent id: ${status.consentId}"
                )
                showPaymentSuccess()
            }

            is AirwallexPaymentStatus.InProgress -> {
                // redirecting
                Log.d(TAG, "Payment is redirecting ${status.paymentIntentId}")
                showPaymentInProgress()
                setLoadingProgress(loading = true, cancellable = true)
            }

            is AirwallexPaymentStatus.Failure -> {
                showPaymentError(status.exception.localizedMessage)
            }

            is AirwallexPaymentStatus.Cancel -> {
                Log.d(TAG, "User cancel the payment")
                showPaymentCancelled()
            }
        }
    }

    override fun getViewModelClass(): Class<UIIntegrationViewModel> {
        return UIIntegrationViewModel::class.java
    }

    override fun refreshButtons(selectedOption: Int) {
        // Update the button list to reflect Express Checkout changes
        adapter.updateButtons(getButtonList())
        // Adjust line margin with tighter spacing for better button positioning
        mBinding.rvContent.post {
            adjustLineMargin(60.dpToPx())
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onResume() {
        super.onResume()
        // Refresh buttons when returning from settings to reflect Express Checkout changes
        val selectedOption = when (mBinding.dropdownView.currentOption) {
            "Recurring" -> 1
            "Recurring and payment" -> 2
            else -> 0
        }
        refreshButtons(selectedOption)
    }

    override fun onLoadingCancelled() {
        mViewModel.stopPolling()
    }

    private fun handlePollingResult(result: PaymentStatusPoller.PollingResult) {
        when (result) {
            is PaymentStatusPoller.PollingResult.Complete -> {
                showAlert("Payment Result", result.description)
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

    companion object {
        private const val TAG = "UIIntegrationActivity"
        const val LAUNCH_PAYMENT_LIST = 1
        const val LAUNCH_CUSTOM_PAYMENT_LIST = 2
        const val LAUNCH_CARD_PAYMENT = 3
        const val LAUNCH_CARD_PAYMENT_DIALOG = 4
        const val LAUNCH_SHIPPING_ADDRESS_DIALOG = 5
    }
}
