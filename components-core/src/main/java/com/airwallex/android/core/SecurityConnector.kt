package com.airwallex.android.core

import android.content.Context

/**
 * Security Connector for Device Fingerprinting
 */
interface SecurityConnector {
    fun initialize(applicationContext: Context)

    fun retrieveSecurityToken(
        sessionId: String,
        securityTokenListener: SecurityTokenListener
    )
}
