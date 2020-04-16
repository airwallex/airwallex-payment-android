package com.airwallex.android;

import android.content.Context;
import android.util.Log;

import com.threatmetrix.TrustDefender.Config;
import com.threatmetrix.TrustDefender.EndNotifier;
import com.threatmetrix.TrustDefender.ProfilingOptions;
import com.threatmetrix.TrustDefender.ProfilingResult;
import com.threatmetrix.TrustDefender.THMStatusCode;
import com.threatmetrix.TrustDefender.TrustDefender;

import java.util.ArrayList;
import java.util.List;

public class AirwallexSecurityConnector implements SecurityConnector {

    private static final String TAG = "TrustDefender";

    interface TrustDefenderListener {
        void onResponse(String sessionId);
    }

    @Override
    public void retrieveSecurityToken(Context applicationContext, TrustDefenderListener trustDefenderListener) {
        Log.d(TAG, "Start init TrustDefender " + TrustDefender.version);
        Config config = new Config().setOrgId("Airwallex").setContext(applicationContext);
        THMStatusCode initStatus = TrustDefender.getInstance().init(config);

        if (initStatus == THMStatusCode.THM_OK || initStatus == THMStatusCode.THM_Already_Initialised) {
            Log.d(TAG, "Successfully init init-ed " + initStatus.getDesc());
            doProfile(trustDefenderListener);
        }
    }

    private void doProfile(TrustDefenderListener trustDefenderListener) {
        List<String> customAttributes = new ArrayList<>();
        customAttributes.add(""); // TODO SUCH AS USER INFO / PAYMENT DATA
        ProfilingOptions options = new ProfilingOptions().setCustomAttributes(customAttributes);
        options.setEndNotifier(new EndNotifier() {

            @Override
            public void complete(ProfilingResult profilingResult) {
                TrustDefender.getInstance().doPackageScan(0);
            }
        });

        THMStatusCode status = TrustDefender.getInstance().doProfileRequest(options);

        if (status == THMStatusCode.THM_OK) {
            String sessionID = TrustDefender.getInstance().getResult().getSessionID();
            Log.d(TAG, "Session ID " + sessionID);
            if (trustDefenderListener != null) {
                trustDefenderListener.onResponse(sessionID);
            }
        }
    }
}
