package com.airwallex.paymentacceptance.ui

import android.content.Context
import android.content.Intent
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.databinding.ActivityApiIntegrationBinding
import com.airwallex.paymentacceptance.ui.base.BaseMvvmActivity
import com.airwallex.paymentacceptance.viewmodel.APIIntegrationViewModel

class APIIntegrationActivity : BaseMvvmActivity<ActivityApiIntegrationBinding, APIIntegrationViewModel>() {

    override fun getViewBinding(): ActivityApiIntegrationBinding {
        return ActivityApiIntegrationBinding.inflate(layoutInflater)
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
    }

    override fun addObserver() {

    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, APIIntegrationActivity::class.java))
        }
    }
}
