package com.airwallex.android.core

import android.content.Context

/**
 * Security Connector for Device Fingerprinting
 */
interface SecurityConnector {

    fun retrieveSecurityToken(
        sessionId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    )
}
