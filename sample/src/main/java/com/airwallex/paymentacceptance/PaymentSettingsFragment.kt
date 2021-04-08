package com.airwallex.paymentacceptance

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
import androidx.preference.*

class PaymentSettingsFragment :
    PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        val preferences = preferenceManager.sharedPreferences
        (findPreference<Preference>(getString(R.string.price)) as? EditTextPreference)?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_DECIMAL
        }
        (findPreference<Preference>(getString(R.string.currency)) as? EditTextPreference)?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        }

        val sdkEnvPref: ListPreference? =
            findPreference(getString(R.string.sdk_env_id)) as? ListPreference?
        if (sdkEnvPref != null && sdkEnvPref.value == null) {
            sdkEnvPref.setValueIndex(0)
        }

        val sdkRecurringPref: ListPreference? =
            findPreference(getString(R.string.sdk_support_recurring)) as? ListPreference?
        if (sdkRecurringPref != null && sdkRecurringPref.value == null) {
            sdkRecurringPref.setValueIndex(0)
        }

        onSharedPreferenceChanged(preferences, getString(R.string.base_url))
        onSharedPreferenceChanged(preferences, getString(R.string.api_key))
        onSharedPreferenceChanged(preferences, getString(R.string.client_id))
        onSharedPreferenceChanged(preferences, getString(R.string.price))
        onSharedPreferenceChanged(preferences, getString(R.string.currency))
        onSharedPreferenceChanged(preferences, getString(R.string.wechat_app_id))
        onSharedPreferenceChanged(preferences, getString(R.string.wechat_app_signature))
        onSharedPreferenceChanged(preferences, getString(R.string.sdk_env_id))
        onSharedPreferenceChanged(preferences, getString(R.string.sdk_support_recurring))
        registerOnSharedPreferenceChangeListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == null) {
            return
        }
        val preference = findPreference<Preference>(key)
        when (key) {
            getString(R.string.base_url) -> preference?.summary = Settings.baseUrl
            getString(R.string.api_key) -> preference?.summary = Settings.apiKey
            getString(R.string.client_id) -> preference?.summary = Settings.clientId
            getString(R.string.price) -> preference?.summary = Settings.price
            getString(R.string.currency) -> preference?.summary = Settings.currency
            getString(R.string.wechat_app_id) -> preference?.summary = Settings.weChatAppId
            getString(R.string.wechat_app_signature) -> preference?.summary = Settings.weChatAppSignature
            getString(R.string.sdk_env_id) -> preference?.summary = Settings.sdkEnv
            getString(R.string.sdk_support_recurring) -> preference?.summary = Settings.recurring
        }
    }

    private fun registerOnSharedPreferenceChangeListener() {
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    fun reset() {
        preferenceManager.sharedPreferences.edit().clear().apply()
        PreferenceManager.setDefaultValues(activity, R.xml.settings, true)
        preferenceScreen.removeAll()
        onCreatePreferences(null, null)
    }
}
