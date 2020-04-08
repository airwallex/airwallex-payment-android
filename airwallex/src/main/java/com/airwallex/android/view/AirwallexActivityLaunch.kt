package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable

internal abstract class AirwallexActivityLaunch<TargetActivity : Activity, ArgsType : AirwallexActivityLaunch.Args> internal constructor(
    private val originalActivity: Activity,
    private val targetActivity: Class<TargetActivity>,
    private val requestCode: Int
) {

    internal fun startForResult(args: ArgsType) {
        val intent = Intent(originalActivity, targetActivity).putExtra(Args.AIRWALLEX_EXTRA, args)
        originalActivity.startActivityForResult(intent, requestCode)
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
