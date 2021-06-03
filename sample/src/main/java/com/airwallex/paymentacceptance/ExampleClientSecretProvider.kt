package com.airwallex.paymentacceptance

import android.text.TextUtils
import com.airwallex.android.AirwallexPlugins
import com.airwallex.android.ClientSecretProvider
import com.airwallex.android.ClientSecretUpdateListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExampleClientSecretProvider : ClientSecretProvider {

    private val api: Api
        get() {
            if (TextUtils.isEmpty(AirwallexPlugins.environment.baseUrl())) {
                throw IllegalArgumentException("Base url should not be null or empty")
            }
            return ApiFactory(AirwallexPlugins.environment.baseUrl()).buildRetrofit().create(Api::class.java)
        }

    override fun createClientSecret(customerId: String, updateListener: ClientSecretUpdateListener) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = kotlin.runCatching { api.createClientSecret(customerId) }
            withContext(Dispatchers.Main) {
                response.fold(
                    onSuccess = {
                        updateListener.onClientSecretUpdate(customerId, it.string())
                    },
                    onFailure = {
                        updateListener.onClientSecretUpdateFailure(it.message ?: "")
                    }
                )
            }
        }
    }
}
