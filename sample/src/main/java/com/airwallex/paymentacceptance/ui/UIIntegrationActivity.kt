package com.airwallex.paymentacceptance.ui

import android.content.Intent
import android.util.Log
import com.airwallex.android.core.Airwallex.Companion.AIRWALLEX_CHECKOUT_SCHEMA
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.databinding.ActivityUiIntegrationBinding
import com.airwallex.paymentacceptance.ui.base.BasePaymentActivity
import com.airwallex.paymentacceptance.viewmodel.UIIntegrationViewModel

/**
 * This Activity demonstrates how to call the payment flow UI provided by Airwallex.
 */
class UIIntegrationActivity :
    BasePaymentActivity<ActivityUiIntegrationBinding, UIIntegrationViewModel>() {

    override fun getViewBinding(): ActivityUiIntegrationBinding {
        return ActivityUiIntegrationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        mBinding.radioGroup.check(R.id.radioPayment)
    }

    override fun initListener() {
        mBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.radioRecurring -> 1
                R.id.radioRecurringAndPayment -> 2
                else -> 0
            }
            mViewModel.updateCheckoutModel(selectedOption)
        }
        mBinding.btnPaymentList.setOnClickListener {
            setLoadingProgress(true)
            //launch the payment list page
            mViewModel.launchPaymentList(this)
        }
        mBinding.btnCardPayment.setOnClickListener {
            setLoadingProgress(true)
            //launch the card payment page
            mViewModel.launchCardPage(this)
        }
        mBinding.btnCardDialogPayment.setOnClickListener {
            setLoadingProgress(true)
            //launch the card payment dialog
            mViewModel.launchCardDialog(this)
        }
        mBinding.btnShipping.setOnClickListener {
            //launch the shipping page
            mViewModel.launchShipping(this)
        }
        mBinding.imSetting.setOnClickListener {
            startActivity(Intent(this, PaymentSettingsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    override fun addObserver() {
        mViewModel.airwallexPaymentStatus.observe(this) { status ->
            handleStatusUpdate(status)
        }
        mViewModel.dialogShowed.observe(this) {
            setLoadingProgress(false)
        }
        mViewModel.airwallexShippingStatus.observe(this) {

        }
        mViewModel.createPaymentIntentError.observe(this) { error ->
            setLoadingProgress(false)
            showAlert(
                getString(R.string.create_payment_intent_failed),
                error ?: getString(R.string.payment_failed_message)
            )
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
    }
}
