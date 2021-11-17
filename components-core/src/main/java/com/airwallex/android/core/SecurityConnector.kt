package com.airwallex.android.core

import android.content.Context

/**
 * Security Connector for Device Fingerprinting
 */
interface SecurityConnector {

    fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    )
}
