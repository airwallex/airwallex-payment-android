package com.airwallex.android.core

import android.util.Base64
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.risk.AirwallexRisk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        CoroutineScope(Dispatchers.Main).launch {
            if (clientSecret != this@TokenManager.clientSecret) {
                this@TokenManager.clientSecret = clientSecret
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
}