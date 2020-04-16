package com.airwallex.android;

import android.content.Context;

public interface SecurityConnector {
    void retrieveSecurityToken(Context applicationContext, String paymentIntentId, String customerId, AirwallexSecurityConnector.TrustDefenderListener trustDefenderListener);
}
