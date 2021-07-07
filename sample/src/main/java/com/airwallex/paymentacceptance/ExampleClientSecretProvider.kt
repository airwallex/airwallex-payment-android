package com.airwallex.paymentacceptance

import android.text.TextUtils
import com.airwallex.android.AirwallexPlugins
import com.airwallex.android.ClientSecretProvider
import com.airwallex.android.ClientSecretUpdateListener
import com.airwallex.android.model.parser.ClientSecretParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ExampleClientSecretProvider : ClientSecretProvider {

    private val api: Api
        get() {
            if (TextUtils.isEmpty(AirwallexPlugins.environment.baseUrl())) {
                throw IllegalArgumentException("Base url should not be null or empty")
            }
            return ApiFactory(AirwallexPlugins.environment.baseUrl()).buildRetrofit()
                .create(Api::class.java)
        }

    override fun createClientSecret(
        customerId: String,
        updateListener: ClientSecretUpdateListener
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = kotlin.runCatching {
                val response = api.authentication(
                    apiKey = Settings.apiKey,
                    clientId = Settings.clientId
                )
                Settings.token = JSONObject(response.string())["token"].toString()
                api.createClientSecret(customerId)
            }
            withContext(Dispatchers.Main) {
                response.fold(
                    onSuccess = {
                        updateListener.onClientSecretUpdate(ClientSecretParser().parse(JSONObject(it.string())))
                    },
                    onFailure = {
                        updateListener.onClientSecretUpdateFailure(it.message ?: "")
                    }
                )
            }
        }
    }
}
