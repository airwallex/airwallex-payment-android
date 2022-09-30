package com.airwallex.android.core

import android.util.Base64
import org.json.JSONObject

object TokenManager {
    private var clientSecret: String? = null
    var accountId: String? = null

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