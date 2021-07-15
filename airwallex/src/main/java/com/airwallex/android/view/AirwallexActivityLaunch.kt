package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment

internal abstract class AirwallexActivityLaunch<TargetActivity : Activity, ArgsType : AirwallexActivityLaunch.Args> internal constructor(
    private val originalActivity: Activity,
    private val fragment: Fragment? = null,
    private val targetActivity: Class<TargetActivity>,
    private val requestCode: Int
) {

    internal constructor(
        activity: Activity,
        targetClass: Class<TargetActivity>,
        requestCode: Int
    ) : this(
        originalActivity = activity,
        fragment = null,
        targetActivity = targetClass,
        requestCode = requestCode
    )

    internal constructor(
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
            internal const val AIRWALLEX_EXTRA: String = "airwallex_activity_args"
        }
    }

    internal interface Result : Parcelable {
        fun toBundle(): Bundle

        companion object {
            internal const val AIRWALLEX_EXTRA: String = "airwallexa_activity_result"
        }
    }
}
