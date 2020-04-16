package com.airwallex.android

import android.content.Context
import com.airwallex.android.AirwallexSecurityConnector.TrustDefenderListener

interface SecurityConnector {
    fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context?,
        trustDefenderListener: TrustDefenderListener?
    )
}
