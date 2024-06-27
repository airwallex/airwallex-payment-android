package com.airwallex.android.ui

import android.app.Activity
import android.app.Application
import android.os.Bundle

abstract class AirwallexActivityLaunchLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }
}