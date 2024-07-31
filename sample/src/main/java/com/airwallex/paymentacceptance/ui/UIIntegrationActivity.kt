package com.airwallex.paymentacceptance.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.databinding.ActivityUiIntegrationBinding
import com.airwallex.paymentacceptance.ui.base.BaseMvvmActivity
import com.airwallex.paymentacceptance.viewmodel.UIIntegrationViewModel

class UIIntegrationActivity : BaseMvvmActivity<ActivityUiIntegrationBinding, UIIntegrationViewModel>() {

    override fun getViewBinding(): ActivityUiIntegrationBinding {
        return ActivityUiIntegrationBinding.inflate(layoutInflater)
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
        private const val TAG = "UIIntegrationActivity"

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, UIIntegrationActivity::class.java))
        }
    }
}
