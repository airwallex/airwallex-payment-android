package com.airwallex.paymentacceptance.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.databinding.ActivityUiIntegrationBinding
import com.airwallex.paymentacceptance.ui.base.BaseMvvmActivity
import com.airwallex.paymentacceptance.viewmodel.UIIntegrationViewModel

class UIIntegrationActivity :
    BaseMvvmActivity<ActivityUiIntegrationBinding, UIIntegrationViewModel>() {

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
            mViewModel.launchPaymentList(this)
        }
        mBinding.btnCardPayment.setOnClickListener {
            setLoadingProgress(true)
            mViewModel.launchCardPage(this)
        }
        mBinding.btnCardDialogPayment.setOnClickListener {
            setLoadingProgress(true)
            mViewModel.launchCardDialog(this)
        }
    }

    override fun addObserver() {
        mViewModel.airwallexPaymentStatus.observe(this) { status ->
            handleStatusUpdate(status)
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

    companion object {
        private const val TAG = "UIIntegrationActivity"

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, UIIntegrationActivity::class.java))
        }
    }
}
