package com.airwallex.android.core

import android.util.Base64
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.risk.AirwallexRisk
import org.json.JSONObject
import kotlin.properties.Delegates

object TokenManager {
    private var clientSecret: String? = null
    var accountId: String? by Delegates.observable(null) { _, _, newId ->
        AnalyticsLogger.updateAccountId(newId)
        AirwallexRisk.setAccountId(newId)
    }

    fun updateClientSecret(clientSecret: String) {
        AirwallexLogger.debug("updateClientSecret: $clientSecret")
        if (clientSecret == this.clientSecret) return

        this.clientSecret = clientSecret
        val body = clientSecret.split(".").getOrNull(1) ?: return

        runCatching {
            val decoded = String(Base64.decode(body, Base64.DEFAULT))
            accountId = JSONObject(decoded).getString("account_id")
        }
    }
}