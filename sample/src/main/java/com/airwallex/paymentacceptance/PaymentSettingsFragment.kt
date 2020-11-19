package com.airwallex.paymentacceptance

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager

class PaymentSettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        val preferences = preferenceManager.sharedPreferences
        /*(findPreference<Preference>(getString(R.string.price)) as? EditTextPreference)?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_DECIMAL
        }
        (findPreference<Preference>(getString(R.string.currency)) as? EditTextPreference)?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        }*/

        val threeDSecurePref: ListPreference? =
            findPreference(getString(R.string.three_d_secure_id)) as? ListPreference?
        if (threeDSecurePref != null && threeDSecurePref.value == null) {
            threeDSecurePref.setValueIndex(0)
        }

        onSharedPreferenceChanged(preferences, getString(R.string.auth_url))
        onSharedPreferenceChanged(preferences, getString(R.string.base_url))
        onSharedPreferenceChanged(preferences, getString(R.string.api_key))
        onSharedPreferenceChanged(preferences, getString(R.string.client_id))
        onSharedPreferenceChanged(preferences, getString(R.string.price))
        onSharedPreferenceChanged(preferences, getString(R.string.currency))
        onSharedPreferenceChanged(preferences, getString(R.string.wechat_app_id))
        onSharedPreferenceChanged(preferences, getString(R.string.wechat_app_signature))
        onSharedPreferenceChanged(preferences, getString(R.string.three_d_secure_id))
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
        val preference = findPreference(key)
        when (key) {
            getString(R.string.auth_url) -> preference?.summary = Settings.authUrl
            getString(R.string.base_url) -> preference?.summary = Settings.baseUrl
            getString(R.string.api_key) -> preference?.summary = Settings.apiKey
            getString(R.string.client_id) -> preference?.summary = Settings.clientId
            getString(R.string.price) -> preference?.summary = Settings.price
            getString(R.string.currency) -> preference?.summary = Settings.currency
            getString(R.string.wechat_app_id) -> preference?.summary = Settings.weChatAppId
            getString(R.string.wechat_app_signature) -> preference?.summary = Settings.weChatAppSignature
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
