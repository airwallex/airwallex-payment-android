package com.airwallex.android;

import android.content.Context;
import android.util.Log;

import com.threatmetrix.TrustDefender.Config;
import com.threatmetrix.TrustDefender.EndNotifier;
import com.threatmetrix.TrustDefender.Profile;
import com.threatmetrix.TrustDefender.ProfilingOptions;
import com.threatmetrix.TrustDefender.TrustDefender;

import java.util.ArrayList;
import java.util.List;

public class AirwallexSecurityConnector implements SecurityConnector {

    private static final String TAG = "TrustDefender";
    private static final String ORG_ID = "1snn5n9w";

    interface TrustDefenderListener {
        void onResponse(String sessionId);
    }

    @Override
    public void retrieveSecurityToken(Context applicationContext, String paymentIntentId, String customerId, TrustDefenderListener trustDefenderListener) {
        Log.d(TAG, "Start init TrustDefender " + TrustDefender.version);
        Config config = new Config().setOrgId(ORG_ID).setContext(applicationContext);

        TrustDefender.getInstance().init(config);

        Log.d(TAG, "Successfully init init-ed");
        doProfile(paymentIntentId, customerId, trustDefenderListener);
    }

    private void doProfile(String paymentIntentId, String customerId, final TrustDefenderListener trustDefenderListener) {
        List<String> customAttributes = new ArrayList<>();
        if (paymentIntentId != null) {
            customAttributes.add(paymentIntentId);
        }
        if (customerId != null) {
            customAttributes.add(customerId);
        }

        ProfilingOptions options = new ProfilingOptions().setCustomAttributes(customAttributes);
        TrustDefender.getInstance().doProfileRequest(options, new EndNotifier() {
            @Override
            public void complete(Profile.Result result) {
                String sessionID = result.getSessionID();
                Log.d(TAG, "sessionID" + sessionID);

                if (trustDefenderListener != null) {
                    trustDefenderListener.onResponse(sessionID);
                }
            }
        });
    }
}
