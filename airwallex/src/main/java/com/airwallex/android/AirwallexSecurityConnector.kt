package com.airwallex.android

import android.content.Context
import com.threatmetrix.TrustDefender.Config
import com.threatmetrix.TrustDefender.ProfilingOptions
import com.threatmetrix.TrustDefender.TrustDefender

/**
 * The implementation of [SecurityConnector] to retrieve the Device Fingerprinting token
 */
class AirwallexSecurityConnector : SecurityConnector {

    interface SecurityTokenListener {
        fun onResponse(sessionId: String?)
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context?,
        securityTokenListener: SecurityTokenListener
    ) {
        Logger.debug(
            TAG,
            "Start init TrustDefender " + TrustDefender.version
        )
        val config = Config().setOrgId(BuildConfig.DEVICE_FINGERPRINT_ORG_ID)
            .setContext(applicationContext)
        TrustDefender.getInstance().init(config)
        Logger.debug(TAG, "Successfully init init-ed")
        doProfile(paymentIntentId, securityTokenListener)
    }

    private fun doProfile(paymentIntentId: String, securityTokenListener: SecurityTokenListener) {
        val fraudSessionId = "$paymentIntentId${System.currentTimeMillis()}"
        val options =
            ProfilingOptions().setSessionID("${BuildConfig.DEVICE_FINGERPRINT_MERCHANT_ID}$fraudSessionId")
        // Fire off the profiling request.
        TrustDefender.getInstance().doProfileRequest(options) { result ->
            val sessionID = result.sessionID
            Logger.debug(
                TAG,
                "Session id =  $sessionID"
            )
            securityTokenListener.onResponse(sessionID)
        }
    }

    companion object {
        private const val TAG = "TrustDefender"
    }
}
