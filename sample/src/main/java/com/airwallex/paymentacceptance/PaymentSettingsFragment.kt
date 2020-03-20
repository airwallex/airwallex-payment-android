package com.airwallex.paymentacceptance

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class PaymentSettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        val preferences = preferenceManager.sharedPreferences
        onSharedPreferenceChanged(preferences, getString(R.string.auth_url))
        onSharedPreferenceChanged(preferences, getString(R.string.base_url))
        onSharedPreferenceChanged(preferences, getString(R.string.api_key))
        onSharedPreferenceChanged(preferences, getString(R.string.client_id))
        onSharedPreferenceChanged(preferences, getString(R.string.wechat_app_id))
        onSharedPreferenceChanged(preferences, getString(R.string.wechat_app_signature))
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
            getString(R.string.auth_url) -> preference?.summary = Settings.authUrl
            getString(R.string.base_url) -> preference?.summary = Settings.baseUrl
            getString(R.string.api_key) -> preference?.summary = Settings.apiKey
            getString(R.string.client_id) -> preference?.summary = Settings.clientId
            getString(R.string.wechat_app_id) -> preference?.summary = Settings.wechatAppId
            getString(R.string.wechat_app_signature) -> preference?.summary =
                Settings.wechatAppSignature
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
