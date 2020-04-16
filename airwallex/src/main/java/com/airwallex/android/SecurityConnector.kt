package com.airwallex.android

import android.content.Context
import com.airwallex.android.AirwallexSecurityConnector.TrustDefenderListener

interface SecurityConnector {
    fun retrieveSecurityToken(
        applicationContext: Context?,
        paymentIntentId: String?,
        customerId: String?,
        trustDefenderListener: TrustDefenderListener?
    )
}
