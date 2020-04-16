package com.airwallex.android

import android.content.Context
import com.threatmetrix.TrustDefender.Config
import com.threatmetrix.TrustDefender.ProfilingOptions
import com.threatmetrix.TrustDefender.TrustDefender

class AirwallexSecurityConnector : SecurityConnector {

    interface TrustDefenderListener {
        fun onResponse(sessionId: String?)
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context?,
        trustDefenderListener: TrustDefenderListener?
    ) {
        Logger.debug(
            TAG,
            "Start init TrustDefender " + TrustDefender.version
        )
        val config = Config()
            .setOrgId(ORG_ID).setContext(applicationContext)
        TrustDefender.getInstance().init(config)
        Logger.debug(TAG, "Successfully init init-ed")
        doProfile(paymentIntentId, trustDefenderListener)
    }

    private fun doProfile(paymentIntentId: String, trustDefenderListener: TrustDefenderListener?) {
        val fraudSessionId = "$paymentIntentId${System.currentTimeMillis()}"
        val options = ProfilingOptions().setSessionID("$MERCHANT_ID$fraudSessionId")
        TrustDefender.getInstance().doProfileRequest(options) { result ->
            val sessionID = result.sessionID
            Logger.debug(
                TAG,
                "sessionID $sessionID"
            )
            trustDefenderListener?.onResponse(sessionID)
        }
    }

    companion object {
        private const val TAG = "TrustDefender"
        private const val ORG_ID = "1snn5n9w"
        private const val MERCHANT_ID = "airwallex_cybs"
    }
}
