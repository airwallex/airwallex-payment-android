package com.airwallex.android.card

import android.content.Context
import com.airwallex.android.core.SecurityConnector
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.log.Logger
import com.threatmetrix.TrustDefender.RL.*
import com.threatmetrix.TrustDefender.RL.TMXProfiling.*
import com.threatmetrix.TrustDefender.RL.TMXProfilingConnections.*
import java.util.concurrent.TimeUnit

/**
 * The implementation of [SecurityConnector] to retrieve the Device Fingerprinting token
 */
class AirwallexSecurityConnector : SecurityConnector {

    private var profilingHandle: TMXProfilingHandle? = null

    /**
     *  Retrieve the SecurityToken from Cardinals under the ID of [PaymentIntent]
     *
     *  @param paymentIntentId ID of [PaymentIntent]
     *  @param applicationContext The Context of Application
     *  @param securityTokenListener The listener of when retrieved the SecurityToken
     */
    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        Logger.debug(TAG, "Start init TrustDefender")
        val profilingConnections: TMXProfilingConnectionsInterface = TMXProfilingConnections()
            .setConnectionTimeout(20, TimeUnit.SECONDS)
            .setRetryTimes(3)
        val config = TMXConfig().setOrgId(BuildConfig.DEVICE_FINGERPRINT_ORG_ID)
            .setContext(applicationContext)
        config.setProfilingConnections(profilingConnections)
        getInstance().init(config)

        Logger.debug(TAG, "Successfully init init-ed")
        doProfile(paymentIntentId, securityTokenListener)
    }

    /**
     * Init was successful or there is a valid instance to be used for further calls. Fire a profile request
     */
    private fun doProfile(paymentIntentId: String, securityTokenListener: SecurityTokenListener) {
        val fraudSessionId = "$paymentIntentId${System.currentTimeMillis()}"
        val sessionID = "${BuildConfig.DEVICE_FINGERPRINT_MERCHANT_ID}$fraudSessionId"
        val options = TMXProfilingOptions().setSessionID(sessionID)
        // Fire off the profiling request.
        profilingHandle = getInstance().profile(options) { result ->
            Logger.debug(
                TAG,
                "Session id: ${result.sessionID}, Session status: ${result.status}"
            )
            profilingHandle?.cancel()
            profilingHandle = null
        }
        Logger.debug(TAG, "Response sessionID $sessionID")
        securityTokenListener.onResponse(sessionID)
    }

    companion object {
        private const val TAG = "TrustDefender"
    }
}
