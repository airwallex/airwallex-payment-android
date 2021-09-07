package com.airwallex.paymentacceptance

import android.text.TextUtils
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.ClientSecretProvider
import com.airwallex.android.core.model.ClientSecret
import com.airwallex.android.core.model.parser.ClientSecretParser
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

    override fun provideClientSecret(customerId: String): ClientSecret {
        val authResponse = api.authenticationSynchronous(
            apiKey = Settings.apiKey,
            clientId = Settings.clientId
        ).execute().body()
        Settings.token = JSONObject(authResponse!!.string())["token"].toString()
        val clientSecretResponse = api.createClientSecretSynchronous(customerId).execute().body()
        return ClientSecretParser().parse(JSONObject(clientSecretResponse!!.string()))
    }
}
