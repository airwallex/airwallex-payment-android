package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment

internal abstract class ActivityStarter<TargetActivity : Activity, ArgsType : ActivityStarter.Args> internal constructor(
    private val activity: Activity,
    private val fragment: Fragment? = null,
    private val targetActivity: Class<TargetActivity>,
    private val requestCode: Int
) {
    protected constructor(
        activity: Activity,
        targetActivity: Class<TargetActivity>,
        requestCode: Int
    ) : this(
        activity = activity,
        fragment = null,
        targetActivity = targetActivity,
        requestCode = requestCode
    )

    internal fun startForResult(args: ArgsType) {
        val intent = Intent(activity, targetActivity)
            .putExtra(Args.AIRWALLEX_EXTRA, args)

        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode)
        } else {
            activity.startActivityForResult(intent, requestCode)
        }
    }

    internal interface Args : Parcelable {
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
