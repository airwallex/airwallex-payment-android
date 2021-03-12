package com.airwallex.android

import android.content.Context
import com.airwallex.android.model.PaymentIntent
import com.threatmetrix.TrustDefender.Config
import com.threatmetrix.TrustDefender.Profile
import com.threatmetrix.TrustDefender.ProfilingOptions
import com.threatmetrix.TrustDefender.TrustDefender

/**
 * The implementation of [SecurityConnector] to retrieve the Device Fingerprinting token
 */
internal class AirwallexSecurityConnector : SecurityConnector {

    private var profilingHandle: Profile.Handle? = null

    /**
     * Retrieve SecurityToken listener
     */
    interface SecurityTokenListener {
        fun onResponse(deviceId: String)
    }

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

    /**
     * Init was successful or there is a valid instance to be used for further calls. Fire a profile request
     */
    private fun doProfile(paymentIntentId: String, securityTokenListener: SecurityTokenListener) {
        val fraudSessionId = "$paymentIntentId${System.currentTimeMillis()}"
        val sessionID = "${BuildConfig.DEVICE_FINGERPRINT_MERCHANT_ID}$fraudSessionId"
        val options = ProfilingOptions().setSessionID(sessionID)
        // Fire off the profiling request.
        profilingHandle = TrustDefender.getInstance().doProfileRequest(options) { result ->
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
