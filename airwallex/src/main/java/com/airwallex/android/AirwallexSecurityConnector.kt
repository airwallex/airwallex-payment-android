package com.airwallex.android

import android.content.Context
import com.threatmetrix.TrustDefender.Config
import com.threatmetrix.TrustDefender.ProfilingOptions
import com.threatmetrix.TrustDefender.TrustDefender
import java.util.*

class AirwallexSecurityConnector : SecurityConnector {

    interface TrustDefenderListener {
        fun onResponse(sessionId: String?)
    }

    override fun retrieveSecurityToken(
        applicationContext: Context?,
        paymentIntentId: String?,
        customerId: String?,
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
        doProfile(paymentIntentId, customerId, trustDefenderListener)
    }

    private fun doProfile(
        paymentIntentId: String?,
        customerId: String?,
        trustDefenderListener: TrustDefenderListener?
    ) {
        val customAttributes: MutableList<String> =
            ArrayList()
        if (paymentIntentId != null) {
            customAttributes.add(paymentIntentId)
        }
        if (customerId != null) {
            customAttributes.add(customerId)
        }
        val options = ProfilingOptions().setCustomAttributes(customAttributes)
        TrustDefender.getInstance().doProfileRequest(options) { result ->
            val sessionID = result.sessionID
            Logger.debug(
                TAG,
                "sessionID$sessionID"
            )
            trustDefenderListener?.onResponse(sessionID)
        }
    }

    companion object {
        private const val TAG = "TrustDefender"
        private const val ORG_ID = "1snn5n9w"
    }
}
