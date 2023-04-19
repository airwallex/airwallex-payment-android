package com.airwallex.android.core

import android.util.Base64
import com.airwallex.android.core.log.AnalyticsLogger
import org.json.JSONObject
import kotlin.properties.Delegates

object TokenManager {
    private var clientSecret: String? = null
    var accountId: String? by Delegates.observable(null) { _, _, newId ->
        AnalyticsLogger.updateAccountId(newId)
    }

    fun updateClientSecret(clientSecret: String) {
        if (clientSecret != this.clientSecret) {
            this.clientSecret = clientSecret
            val body = clientSecret.split(".").getOrNull(1)
            if (body != null) {
                runCatching {
                    accountId = JSONObject(
                        String(
                            Base64.decode(
                                body,
                                Base64.DEFAULT
                            )
                        )
                    ).getString("account_id")
                }
            }
        }
    }
}