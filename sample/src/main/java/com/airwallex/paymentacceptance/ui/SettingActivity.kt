package com.airwallex.paymentacceptance.ui

import android.view.View
import androidx.appcompat.app.AlertDialog
import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.databinding.ActivitySettingBinding
import com.airwallex.paymentacceptance.ui.base.BasePaymentActivity
import com.airwallex.paymentacceptance.viewmodel.SettingViewModel

class SettingActivity : BasePaymentActivity<ActivitySettingBinding, SettingViewModel>() {

    override fun initView() {

        val environmentOptions = resources.getStringArray(R.array.array_sdk_env).toList()
        var triggerOptions = resources.getStringArray(R.array.array_next_trigger_by).toList()
        if (Settings.checkoutMode == AirwallexCheckoutMode.PAYMENT) {
            triggerOptions = arrayListOf("Customer")
            mBinding.swCVC.visibility = View.GONE
        }
        mBinding.selectViewEnvironment.setOptions(environmentOptions)
        mBinding.selectViewTrigger.setOptions(triggerOptions)

        mBinding.selectViewEnvironment.setSelectOption(Settings.sdkEnv)
        mBinding.selectViewTrigger.setSelectOption(Settings.nextTriggerBy)

        if (Settings.checkoutMode == AirwallexCheckoutMode.PAYMENT) {
            mBinding.selectViewTrigger.setSelectOption("Customer")
        }

        mBinding.etPrice.setText(Settings.price)
        mBinding.etCurrency.setText(Settings.currency)
        mBinding.etCountryCode.setText(Settings.countryCode)

        mBinding.etCustomerId.setText(Settings.cachedCustomerId ?: "")
        mBinding.etAPIKey.setText(Settings.apiKey)
        mBinding.etClientId.setText(Settings.clientId)
        mBinding.etWeChatAppId.setText(Settings.weChatAppId)

        mBinding.swAutoCapture.setChecked(Settings.autoCapture == "Enabled")
        mBinding.swEmail.setChecked(Settings.requiresEmail == "True")
        mBinding.swCVC.setChecked(Settings.requiresCVC == "True")

        mBinding.etReturnUrl.setText(Settings.returnUrl)
    }

    override fun initListener() {
        mBinding.selectViewEnvironment.setOnOptionSelectedCallback {
            Settings.sdkEnv = it
        }
        mBinding.selectViewTrigger.setOnOptionSelectedCallback {
            Settings.nextTriggerBy = it
        }

        mBinding.flBack.setOnClickListener {
            finish()
        }
        mBinding.etCustomerId.setActionClickListener {
            if(mBinding.etAPIKey.getText().isEmpty() || mBinding.etClientId.getText().isEmpty()) {
                showApiClientInputDialog()
                return@setActionClickListener
            }
            mViewModel.generateCustomerId()
        }
        mBinding.btnSave.setOnClickListener {
            Settings.price = mBinding.etPrice.getText()
            Settings.currency = mBinding.etCurrency.getText()
            Settings.countryCode = mBinding.etCountryCode.getText()

            Settings.cachedCustomerId = mBinding.etCustomerId.getText()
            Settings.apiKey = mBinding.etAPIKey.getText()
            Settings.clientId = mBinding.etClientId.getText()
            Settings.weChatAppId = mBinding.etWeChatAppId.getText()
            Settings.returnUrl = mBinding.etReturnUrl.getText()

            Settings.autoCapture = if (mBinding.swAutoCapture.isChecked()) "Enabled" else "Disabled"
            Settings.requiresEmail = if (mBinding.swEmail.isChecked()) "True" else "False"
            Settings.requiresCVC = if (mBinding.swCVC.isChecked()) "True" else "False"
            showAlert("", "settings saved") {
                finish()
            }
        }

        mBinding.titleView.setOnButtonClickListener {
            mViewModel.clearSetting()
            mBinding.etPrice.setText("1")
            mBinding.etCurrency.setText("HKD")
            mBinding.etCountryCode.setText("HK")
            mBinding.etCustomerId.setText("")
            mBinding.etAPIKey.setText("")
            mBinding.etClientId.setText("")
            mBinding.etWeChatAppId.setText("")
            mBinding.swAutoCapture.setChecked(false)
            mBinding.swEmail.setChecked(false)
            mBinding.swCVC.setChecked(false)
            mBinding.selectViewEnvironment.setSelectOption("DEMO")
            mBinding.selectViewTrigger.setSelectOption("Merchant")
            mBinding.etReturnUrl.setText("")
            showAlert("", "settings cleared") {
                finish()
            }
        }
    }

    override fun addObserver() {
        mViewModel.customerId.observe(this) {
            if (it.first) {
                mBinding.etCustomerId.setText(it.second ?: "")
            } else {
                showAlert(
                    getString(R.string.generate_customer_failed),
                    it.second ?: getString(R.string.generate_customer_failed_message)
                )
            }
        }
    }

    override fun getViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    private fun showApiClientInputDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enter Required Information")
            .setMessage("Please enter API Key and Client ID")
            .setPositiveButton("OK") { _, _ ->
            }
            .setCancelable(false)
            .show()
    }
}