package com.airwallex.paymentacceptance.ui

import android.content.Intent
import android.util.Log
import com.airwallex.android.core.Airwallex.Companion.AIRWALLEX_CHECKOUT_SCHEMA
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.ui.base.BasePaymentTypeActivity
import com.airwallex.paymentacceptance.ui.bean.ButtonItem
import com.airwallex.paymentacceptance.viewmodel.UIIntegrationViewModel

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
        mViewModel.airwallexPaymentStatus.observe(this) { status ->
            handleStatusUpdate(status)
        }
        mViewModel.dialogShowed.observe(this) {
            setLoadingProgress(false)
        }
        mViewModel.airwallexShippingStatus.observe(this) {

        }
    }

    override fun getButtonList(): List<ButtonItem> {
        return listOf(
            ButtonItem(LAUNCH_PAYMENT_LIST, "Launch payment list"),
            ButtonItem(LAUNCH_CUSTOM_PAYMENT_LIST, "Launch custom payment list"),
            ButtonItem(LAUNCH_CARD_PAYMENT, "Launch card payment"),
            ButtonItem(LAUNCH_CARD_PAYMENT_DIALOG, "Launch card payment (dialog)"),
            ButtonItem(LAUNCH_SHIPPING_ADDRESS_DIALOG, "Launch shipping address (dialog)")
        )
    }

    override fun handleBtnClick(id: Int) {
        when (id) {
            LAUNCH_PAYMENT_LIST -> {
                setLoadingProgress(true)
                mViewModel.launchPaymentList(this)
            }

            LAUNCH_CUSTOM_PAYMENT_LIST -> {
                setLoadingProgress(true)
                mViewModel.launchCustomPaymentList(this)
            }

            LAUNCH_CARD_PAYMENT -> {
                setLoadingProgress(true)
                mViewModel.launchCardPage(this)
            }

            LAUNCH_CARD_PAYMENT_DIALOG -> {
                setLoadingProgress(true)
                mViewModel.launchCardDialog(this)
            }

            LAUNCH_SHIPPING_ADDRESS_DIALOG -> {
                mViewModel.launchShipping(this)
            }
        }
    }


    private fun handleStatusUpdate(status: AirwallexPaymentStatus) {
        setLoadingProgress(false)
        when (status) {
            is AirwallexPaymentStatus.Success -> {
                Log.d(TAG, "Payment success ${status.paymentIntentId}")
                showPaymentSuccess()
            }

            is AirwallexPaymentStatus.InProgress -> {
                // redirecting
                Log.d(TAG, "Payment is redirecting ${status.paymentIntentId}")
                showPaymentInProgress()
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

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.scheme == AIRWALLEX_CHECKOUT_SCHEMA) {
            showAlert(
                getString(R.string.payment_successful),
                getString(R.string.payment_successful_message)
            )
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
