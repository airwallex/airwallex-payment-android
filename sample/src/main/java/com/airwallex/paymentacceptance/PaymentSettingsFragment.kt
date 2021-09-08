package com.airwallex.paymentacceptance

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType.*
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.android.core.AirwallexPlugins
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*
import kotlin.system.exitProcess

class PaymentSettingsFragment :
    PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val api: Api
        get() {
            if (TextUtils.isEmpty(AirwallexPlugins.environment.baseUrl())) {
                throw IllegalArgumentException("Base url should not be null or empty")
            }
            return ApiFactory(AirwallexPlugins.environment.baseUrl()).buildRetrofit()
                .create(Api::class.java)
        }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        activity?.runOnUiThread {
            if (throwable is HttpException) {
                showCreateCustomerError(
                    throwable.response()?.errorBody()?.string() ?: throwable.localizedMessage
                )
            } else {
                showCreateCustomerError(throwable.localizedMessage)
            }
        }
    }

    private fun showCreateCustomerError(error: String? = null) {
        showAlert(
            getString(R.string.generate_customer_failed),
            error ?: getString(R.string.generate_customer_failed_message)
        )
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }

    companion object {
        const val TAG = "PaymentSettingsFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        val preferences = preferenceManager.sharedPreferences
        (findPreference<Preference>(getString(R.string.price)) as? EditTextPreference)?.setOnBindEditTextListener { editText ->
            editText.inputType = TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_DECIMAL
        }
        (findPreference<Preference>(getString(R.string.currency)) as? EditTextPreference)?.setOnBindEditTextListener { editText ->
            editText.inputType = TYPE_TEXT_FLAG_CAP_CHARACTERS
        }

        val sdkEnvPref: ListPreference? =
            findPreference(getString(R.string.sdk_env_id)) as? ListPreference?
        if (sdkEnvPref != null && sdkEnvPref.value == null) {
            // Default Staging
            sdkEnvPref.setValueIndex(0)
        }

        val checkoutModePref: ListPreference? =
            findPreference(getString(R.string.checkout_mode)) as? ListPreference?
        if (checkoutModePref != null && checkoutModePref.value == null) {
            checkoutModePref.setValueIndex(0)
        }

        val nextTriggerByPref: ListPreference? =
            findPreference(getString(R.string.next_trigger_by)) as? ListPreference?
        if (nextTriggerByPref != null && nextTriggerByPref.value == null) {
            nextTriggerByPref.setValueIndex(0)
        }
        nextTriggerByPref?.isEnabled =
            !(checkoutModePref?.value == AirwallexCheckoutMode.PAYMENT.name && nextTriggerByPref != null)

        val requireCVCPref: ListPreference? =
            findPreference(getString(R.string.requires_cvc)) as? ListPreference?
        if (requireCVCPref != null && requireCVCPref.value == null) {
            requireCVCPref.setValueIndex(0)
        }
        requireCVCPref?.isEnabled =
            !(checkoutModePref?.value == AirwallexCheckoutMode.PAYMENT.name && requireCVCPref != null)

        val generateCustomerPref: Preference? =
            findPreference(getString(R.string.generate_customer)) as? Preference?
        generateCustomerPref?.summary = Settings.cachedCustomerId
        generateCustomerPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            viewLifecycleOwner.lifecycleScope.safeLaunch(
                Dispatchers.IO,
                coroutineExceptionHandler
            ) {
                val response = api.authentication(
                    apiKey = Settings.apiKey,
                    clientId = Settings.clientId
                )
                Settings.token = JSONObject(response.string())["token"].toString()

                val customerResponse = api.createCustomer(
                    mutableMapOf(
                        "request_id" to UUID.randomUUID().toString(),
                        "merchant_customer_id" to UUID.randomUUID().toString(),
                        "first_name" to "John",
                        "last_name" to "Doe",
                        "email" to "john.doe@airwallex.com",
                        "phone_number" to "13800000000",
                        "additional_info" to mapOf(
                            "registered_via_social_media" to false,
                            "registration_date" to "2019-09-18",
                            "first_successful_order_date" to "2019-09-18"
                        ),
                        "metadata" to mapOf(
                            "id" to 1
                        )
                    )
                )
                val customerId = JSONObject(customerResponse.string())["id"].toString()
                Settings.cachedCustomerId = customerId
                withContext(Dispatchers.Main) {
                    generateCustomerPref?.summary = Settings.cachedCustomerId
                }
            }
            true
        }

        val clearCustomerPref: Preference? =
            findPreference(getString(R.string.clear_customer)) as? Preference?
        clearCustomerPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            clearCustomerId()
            Toast.makeText(context, R.string.customer_cleared, Toast.LENGTH_SHORT).show()
            true
        }

        toggleNextTriggerByStatus()

        onSharedPreferenceChanged(preferences, getString(R.string.api_key))
        onSharedPreferenceChanged(preferences, getString(R.string.client_id))
        onSharedPreferenceChanged(preferences, getString(R.string.price))
        onSharedPreferenceChanged(preferences, getString(R.string.currency))
        onSharedPreferenceChanged(preferences, getString(R.string.wechat_app_id))
        onSharedPreferenceChanged(preferences, getString(R.string.sdk_env_id))
        onSharedPreferenceChanged(preferences, getString(R.string.checkout_mode))
        onSharedPreferenceChanged(preferences, getString(R.string.next_trigger_by))
        onSharedPreferenceChanged(preferences, getString(R.string.requires_cvc))
        registerOnSharedPreferenceChangeListener()
    }

    private fun clearCustomerId() {
        Settings.cachedCustomerId = ""
        val generateCustomerPref: Preference? =
            findPreference(getString(R.string.generate_customer)) as? Preference?
        generateCustomerPref?.summary = ""
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
            getString(R.string.api_key) -> preference?.summary = Settings.apiKey
            getString(R.string.client_id) -> preference?.summary = Settings.clientId
            getString(R.string.price) -> {
                preference?.summary = Settings.price
                (preference as EditTextPreference).setOnBindEditTextListener { editText ->
                    editText.inputType = TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_DECIMAL
                    editText.setSelection(editText.length())
                }
            }
            getString(R.string.currency) -> {
                preference?.summary = Settings.currency
                (preference as EditTextPreference).setOnBindEditTextListener { editText ->
                    editText.inputType = TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_CAP_CHARACTERS
                    editText.setSelection(editText.length())
                }
            }
            getString(R.string.wechat_app_id) -> preference?.summary = Settings.weChatAppId
            getString(R.string.sdk_env_id) -> preference?.summary = Settings.sdkEnv
            getString(R.string.checkout_mode) -> preference?.summary = Settings.checkoutMode
            getString(R.string.next_trigger_by) -> preference?.summary = Settings.nextTriggerBy
            getString(R.string.requires_cvc) -> preference?.summary = Settings.requiresCVC
        }
        toggleNextTriggerByStatus()
    }

    private fun toggleNextTriggerByStatus() {
        val checkoutModePref: ListPreference? =
            findPreference(getString(R.string.checkout_mode)) as? ListPreference?
        val nextTriggerByPref: ListPreference? =
            findPreference(getString(R.string.next_trigger_by)) as? ListPreference?
        val requireCVCByPref: ListPreference? =
            findPreference(getString(R.string.requires_cvc)) as? ListPreference?
        nextTriggerByPref?.isEnabled =
            !(checkoutModePref?.value?.uppercase(Locale.getDefault()) == AirwallexCheckoutMode.PAYMENT.name && nextTriggerByPref != null)
        requireCVCByPref?.isEnabled =
            !(checkoutModePref?.value?.uppercase(Locale.getDefault()) == AirwallexCheckoutMode.PAYMENT.name && requireCVCByPref != null)
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

    private fun doRestart(c: Context) {
        try {
            val pm: PackageManager = c.packageManager
            val activity = pm.getLaunchIntentForPackage(
                c.packageName
            )
            if (activity != null) {
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                // create a pending intent so the application is restarted after System.exit(0) was called.
                // We use an AlarmManager to call this intent in 100ms
                val mPendingIntentId = 223344
                val mPendingIntent = PendingIntent
                    .getActivity(
                        c, mPendingIntentId, activity,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    )
                val mgr = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
                exitProcess(0)
            } else {
                Log.e(TAG, "Was not able to restart application, activity null")
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Was not able to restart application")
        }
    }
}
