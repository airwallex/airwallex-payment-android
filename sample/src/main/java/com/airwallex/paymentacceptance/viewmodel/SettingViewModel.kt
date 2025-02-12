package com.airwallex.paymentacceptance.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SettingViewModel : BaseViewModel() {
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

    fun generateCustomerId() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            Settings.cachedCustomerId = ""
            val customerId = getCustomerIdFromServer(false)
            withContext(Dispatchers.Main) {
                _customerId.value = Pair(true, customerId)
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
        Settings.sdkEnv = "DEMO"
        Settings.nextTriggerBy = "Merchant"
        Settings.returnUrl = ""
    }
}
