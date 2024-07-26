package com.airwallex.android.threedsecurity

import android.content.Context
import com.airwallex.android.core.SecurityConnector
import com.airwallex.android.core.SecurityTokenListener
import com.airwallex.android.core.log.AirwallexLogger
import com.lexisnexisrisk.threatmetrix.TMXConfig
import com.lexisnexisrisk.threatmetrix.TMXProfiling.getInstance
import com.lexisnexisrisk.threatmetrix.TMXProfilingConnectionsInterface
import com.lexisnexisrisk.threatmetrix.TMXProfilingHandle
import com.lexisnexisrisk.threatmetrix.TMXProfilingOptions
import com.lexisnexisrisk.threatmetrix.tmxprofilingconnections.TMXProfilingConnections
import java.util.concurrent.TimeUnit

/**
 * The implementation of [SecurityConnector] to retrieve the Device Fingerprinting token
 */
class AirwallexSecurityConnector : SecurityConnector {

    private var profilingHandle: TMXProfilingHandle? = null

    /**
     *  Retrieve the SecurityToken from Cardinals under the ID of Airwallex session
     *
     *  @param sessionId ID of an Airwallex session
     *  @param applicationContext The Context of Application
     *  @param securityTokenListener The listener of when retrieved the SecurityToken
     */
    override fun retrieveSecurityToken(
        sessionId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        AirwallexLogger.debug("Start init TrustDefender")
        val profilingConnections: TMXProfilingConnectionsInterface = TMXProfilingConnections()
            .setConnectionTimeout(20, TimeUnit.SECONDS)
            .setRetryTimes(3)
        val config = TMXConfig().setOrgId(BuildConfigHelper.deviceFingerprintOrgId)
            .setContext(applicationContext)
        config.setProfilingConnections(profilingConnections)
        getInstance().init(config)

        AirwallexLogger.debug("Successfully init init-ed")
        doProfile(sessionId, securityTokenListener)
    }

    /**
     * Init was successful or there is a valid instance to be used for further calls. Fire a profile request
     */
    private fun doProfile(sessionId: String, securityTokenListener: SecurityTokenListener) {
        val options = TMXProfilingOptions().setSessionID(sessionId)
        // Fire off the profiling request.
        profilingHandle = getInstance().profile(options) { result ->
            AirwallexLogger.debug(
                "Session id: ${result.sessionID}, Session status: ${result.status}"
            )
            profilingHandle?.cancel()
            profilingHandle = null
        }
        AirwallexLogger.debug("Response sessionID $sessionId")
        securityTokenListener.onResponse(sessionId)
    }
}
