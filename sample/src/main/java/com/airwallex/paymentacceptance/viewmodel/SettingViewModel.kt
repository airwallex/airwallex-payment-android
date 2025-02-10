package com.airwallex.paymentacceptance.viewmodel

import android.content.SharedPreferences
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.paymentacceptance.SampleApplication
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.api.Api
import com.airwallex.paymentacceptance.api.ApiFactory
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.util.UUID

class SettingViewModel : BaseViewModel() {

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(SampleApplication.instance)
    }

    private val _customerId = MutableLiveData<Pair<Boolean, String?>>()
    val customerId: LiveData<Pair<Boolean, String?>> = _customerId

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is HttpException) {
            _customerId.value = Pair(
                false,
                throwable.response()?.errorBody()?.string() ?: throwable.localizedMessage
            )
        } else {
            _customerId.value = Pair(false, throwable.localizedMessage)
        }

    }

    override fun init(activity: ComponentActivity) {

    }

    fun saveSetting(key: String, value: String) {
        sharedPreferences.edit().apply {
            putString(key, value)
            apply()
        }
    }

    fun generateCustomerId() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            Settings.cachedCustomerId = ""
            val customerId = getCustomerIdFromServer()
            Settings.cachedCustomerId = customerId
            withContext(Dispatchers.Main) {
                _customerId.value = Pair(true, customerId)
                saveSetting(Settings.CUSTOMER_ID, customerId)
            }
        }
    }

    fun clearSetting() {
        Settings.price = "1"
        Settings.currency = "HKD"
        Settings.countryCode = "HK"
        Settings.cachedCustomerId = ""
        Settings.apiKey = ""
        Settings.clientId = ""
        Settings.weChatAppId = ""
        Settings.autoCapture = "Disabled"
        Settings.requiresEmail = "False"
        Settings.requiresCVC = "False"
        Settings.sdkEnv = "DEMO"
        Settings.nextTriggerBy = "Merchant"
        Settings.returnUrl = ""
    }
}
