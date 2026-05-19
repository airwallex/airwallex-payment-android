package com.airwallex.paymentacceptance.ui

import android.content.Intent
import android.os.Process
import androidx.appcompat.app.AlertDialog
import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.android.core.RequiredBillingContactField
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.SampleApplication
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.databinding.ActivitySettingBinding
import com.airwallex.paymentacceptance.ui.base.BasePaymentActivity
import com.airwallex.paymentacceptance.viewmodel.SettingViewModel

class SettingActivity : BasePaymentActivity<ActivitySettingBinding, SettingViewModel>() {

    // `null` means "merchant hasn't picked — derive from legacy
    // setRequireBillingInformation / setRequireEmail flags."
    // Empty set means "hide billing section entirely."
    // Non-empty set means "collect exactly these fields."
    private var selectedBillingFields: Set<RequiredBillingContactField>? = null

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

        mBinding.sw3DS.setChecked(Settings.force3DS == "True")
        mBinding.swAutoCapture.setChecked(Settings.autoCapture == "Enabled")
        mBinding.swExpressCheckout.setChecked(Settings.expressCheckout == "Enabled")
        mBinding.swUseSession.setChecked(Settings.useSession == "Enabled")
        mBinding.swEmail.setChecked(Settings.requiresEmail == "True")

        mBinding.lvBillingFields.setTitleText("Required Billing Fields")
        selectedBillingFields = Settings.requiredBillingContactFields
        renderBillingFieldsSummary()
        mBinding.lvBillingFields.setOnClickListener { showBillingFieldsDialog() }

        mBinding.etReturnUrl.setText(Settings.returnUrl)
    }

    override fun initListener() {
        mBinding.flBack.setOnClickListener {
            finish()
        }
        mBinding.etCustomerId.setActionClickListener {
            val selectedEnv = mBinding.selectViewEnvironment.currentOption
            if (selectedEnv != Settings.sdkEnv) {
                showAlert(
                    "",
                    "The app needs to restart before generating customer ID in the new environment.",
                    positiveButtonText = "Restart",
                    negativeButtonText = "Cancel",
                    onPositive = { saveAndRestart() }
                )
            } else {
                mViewModel.generateCustomerId()
            }
        }
        mBinding.selectViewEnvironment.setOnOptionSelectedCallback { selectedEnv ->
            // Only load cached values, don't save environment yet
            loadCachedValuesForEnvironment(selectedEnv)
        }
        mBinding.btnSave.setOnClickListener {
            if (mBinding.selectViewEnvironment.currentOption != Settings.sdkEnv) {
                showAlert(
                    "",
                    "The app will restart to apply the new environment."
                ) {
                    saveAndRestart()
                }
            } else {
                saveAllSettings()
                showAlert("", "settings saved") {
                    finish()
                }
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
            mBinding.sw3DS.setChecked(false)
            mBinding.swAutoCapture.setChecked(false)
            mBinding.swExpressCheckout.setChecked(false)
            mBinding.swUseSession.setChecked(true)
            mBinding.swEmail.setChecked(false)
            selectedBillingFields = null
            renderBillingFieldsSummary()
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

    private fun saveAndRestart() {
        saveAllSettings()
        startActivity(Intent(this, RestartActivity::class.java))
        Process.killProcess(Process.myPid())
    }

    private fun loadCachedValuesForEnvironment(env: String) {
        mBinding.etCustomerId.setText(Settings.getCachedCustomerIdForEnv(env))
        mBinding.etAPIKey.setText(Settings.getApiKeyForEnv(env))
        mBinding.etClientId.setText(Settings.getClientIdForEnv(env))
    }

    private fun saveAllSettings() {
        val selectedEnv = mBinding.selectViewEnvironment.currentOption

        // Save environment selection
        Settings.sdkEnv = selectedEnv

        Settings.nextTriggerBy = mBinding.selectViewTrigger.currentOption
        Settings.paymentLayout = mBinding.selectPaymentLayout.currentOption

        Settings.price = mBinding.etPrice.getText()
        Settings.currency = mBinding.etCurrency.getText()
        Settings.countryCode = mBinding.etCountryCode.getText()

        // Save per-environment values to cache
        Settings.saveCachedCustomerIdForEnv(selectedEnv, mBinding.etCustomerId.getText())
        Settings.saveApiKeyForEnv(selectedEnv, mBinding.etAPIKey.getText())
        Settings.saveClientIdForEnv(selectedEnv, mBinding.etClientId.getText())

        Settings.weChatAppId = mBinding.etWeChatAppId.getText()
        Settings.returnUrl = mBinding.etReturnUrl.getText()

        Settings.force3DS = if (mBinding.sw3DS.isChecked()) "True" else "False"
        Settings.autoCapture = if (mBinding.swAutoCapture.isChecked()) "Enabled" else "Disabled"
        Settings.expressCheckout = if (mBinding.swExpressCheckout.isChecked()) "Enabled" else "Disabled"
        Settings.useSession = if (mBinding.swUseSession.isChecked()) "Enabled" else "Disabled"
        Settings.requiresEmail = if (mBinding.swEmail.isChecked()) "True" else "False"
        Settings.requiredBillingContactFields = selectedBillingFields
        Settings.flush()
    }

    private fun renderBillingFieldsSummary() {
        val current = selectedBillingFields
        val summary = when {
            current == null -> "Unset (derived from legacy flags)"
            current.isEmpty() -> "None (billing section hidden)"
            else -> current.joinToString(", ") { it.name }
        }
        mBinding.lvBillingFields.setSelectedText(summary)
    }

    private fun showBillingFieldsDialog() {
        val options = RequiredBillingContactField.values()
        val labels = options.map { it.name }.toTypedArray()
        // Pre-tick the boxes the user previously chose; nothing if currently unset.
        // Unset  → null = derive from legacy setRequireBillingInformation/Email
        val checked = BooleanArray(options.size) {
            options[it] in selectedBillingFields.orEmpty()
        }
        AlertDialog.Builder(this)
            .setTitle("Required Billing Fields")
            .setMultiChoiceItems(labels, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton("OK") { _, _ ->
                selectedBillingFields = options
                    .filterIndexed { i, _ -> checked[i] }
                    .toSet()
                renderBillingFieldsSummary()
            }
            .setNeutralButton("Unset") { _, _ ->
                selectedBillingFields = null
                renderBillingFieldsSummary()
            }
            .setNegativeButton("Cancel", null)
            .show()
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