package com.airwallex.android;

import android.content.Context;

public interface SecurityConnector {
    void retrieveSecurityToken(Context applicationContext, AirwallexSecurityConnector.TrustDefenderListener trustDefenderListener);
}
