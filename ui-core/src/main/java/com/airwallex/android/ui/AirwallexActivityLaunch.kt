package com.airwallex.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment

abstract class AirwallexActivityLaunch<TargetActivity : Activity, ArgsType : AirwallexActivityLaunch.Args> constructor(
    private val originalActivity: Activity,
    private val fragment: Fragment? = null,
    private val targetActivity: Class<TargetActivity>,
    private val requestCode: Int
) {

    constructor(
        activity: Activity,
        targetClass: Class<TargetActivity>,
        requestCode: Int
    ) : this(
        originalActivity = activity,
        fragment = null,
        targetActivity = targetClass,
        requestCode = requestCode
    )

    constructor(
        fragment: Fragment,
        targetClass: Class<TargetActivity>,
        requestCode: Int
    ) : this(
        originalActivity = fragment.requireActivity(),
        fragment = fragment,
        targetActivity = targetClass,
        requestCode = requestCode
    )

    fun startForResult(args: ArgsType) {
        val intent = Intent(originalActivity, targetActivity).putExtra(Args.AIRWALLEX_EXTRA, args)
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode)
        } else {
            originalActivity.startActivityForResult(intent, requestCode)
        }
    }

    interface Args : Parcelable {
        companion object {
            const val AIRWALLEX_EXTRA: String = "airwallex_activity_args"
        }
    }

    interface Result : Parcelable {
        fun toBundle(): Bundle

        companion object {
            const val AIRWALLEX_EXTRA: String = "airwallexa_activity_result"
        }
    }
}
