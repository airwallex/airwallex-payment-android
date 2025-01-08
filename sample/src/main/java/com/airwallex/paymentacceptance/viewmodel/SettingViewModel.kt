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

    private val api: Api
        get() {
            if (TextUtils.isEmpty(AirwallexPlugins.environment.baseUrl())) {
                throw IllegalArgumentException("Base url should not be null or empty")
            }
            return ApiFactory(AirwallexPlugins.environment.baseUrl()).buildRetrofit()
                .create(Api::class.java)
        }

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
            try {
                // Authenticate and get token
                val response = api.authentication(
                    apiKey = Settings.apiKey,
                    clientId = Settings.clientId
                )
                Settings.token = JSONObject(response.string())["token"].toString()

                // Create customer and retrieve customer ID
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
                    _customerId.value = Pair(true, customerId)
                    saveSetting(Settings.CUSTOMER_ID, customerId)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _customerId.value = Pair(false, e.localizedMessage)
                }
            }
        }
    }

    fun clearSetting() {
        Settings.price = ""
        Settings.currency = ""
        Settings.countryCode = ""
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
