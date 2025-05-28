package com.airwallex.paymentacceptance.ui

import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.SampleApplication
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.databinding.ActivitySettingBinding
import com.airwallex.paymentacceptance.ui.base.BasePaymentActivity
import com.airwallex.paymentacceptance.viewmodel.SettingViewModel

class SettingActivity : BasePaymentActivity<ActivitySettingBinding, SettingViewModel>() {

    override fun initView() {

        val environmentOptions = resources.getStringArray(R.array.array_sdk_env).toList()
        var triggerOptions = resources.getStringArray(R.array.array_next_trigger_by).toList()
        val paymentLayoutOptions = resources.getStringArray(R.array.array_payment_layout).toList()

        if (Settings.checkoutMode == AirwallexCheckoutMode.PAYMENT) {
            triggerOptions = arrayListOf("Customer")
        }
        mBinding.selectViewEnvironment.setOptions(environmentOptions)
        mBinding.selectViewTrigger.setOptions(triggerOptions)
        mBinding.selectPaymentLayout.setOptions(paymentLayoutOptions)

        mBinding.selectViewEnvironment.setSelectOption(Settings.sdkEnv)
        mBinding.selectViewTrigger.setSelectOption(Settings.nextTriggerBy)
        mBinding.selectPaymentLayout.setSelectOption(Settings.paymentLayout)

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

        mBinding.etReturnUrl.setText(Settings.returnUrl)
    }

    override fun initListener() {
        mBinding.flBack.setOnClickListener {
            finish()
        }
        mBinding.etCustomerId.setActionClickListener {
            mViewModel.generateCustomerId()
        }
        mBinding.btnSave.setOnClickListener {
            Settings.sdkEnv = mBinding.selectViewEnvironment.currentOption
            Settings.nextTriggerBy = mBinding.selectViewTrigger.currentOption
            Settings.paymentLayout = mBinding.selectPaymentLayout.currentOption

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
            Settings.cachedCustomerId = mBinding.etCustomerId.getText()
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
            mBinding.selectViewEnvironment.setSelectOption("DEMO")
            mBinding.selectViewTrigger.setSelectOption("Merchant")
            mBinding.selectPaymentLayout.setSelectOption("Tab")
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

    override fun getViewModelClass(): Class<SettingViewModel> {
        return SettingViewModel::class.java
    }

    override fun onDestroy() {
        SampleApplication.instance.configAirwallex()
        super.onDestroy()
    }
}