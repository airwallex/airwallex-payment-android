package com.airwallex.paymentacceptance.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
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

    }

    override fun initListener() {
        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.radioPayment -> "Payment"
                R.id.radioRecurring -> "Recurring"
                R.id.radioRecurringAndPayment -> "Recurring and Payment"
                else -> "None"
            }
            Toast.makeText(this, "Changed selection: $selectedOption", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, APIIntegrationActivity::class.java))
        }
    }
}
