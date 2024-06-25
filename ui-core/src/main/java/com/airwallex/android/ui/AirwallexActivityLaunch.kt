package com.airwallex.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

abstract class AirwallexActivityLaunch<TargetActivity : Activity, ArgsType : AirwallexActivityLaunch.Args> constructor(
    private val originalActivity: Activity,
    private val fragment: Fragment? = null,
    private val targetActivity: Class<TargetActivity>,
    private val requestCode: Int
) {
    companion object {
        private var resultLauncher: ActivityResultLauncher<Intent>? = null
        private var launchObserver: LifecycleObserver? = null
        private var launchRequestCode = 0

        fun registerForActivityResult(
            lifecycleOwner: LifecycleOwner,
            callBack: (requestCode: Int, activityResult: ActivityResult) -> Unit
        ) {
            val activityResult = ActivityResultCallback<ActivityResult> { result ->
                callBack.invoke(launchRequestCode, result)
            }
            launchObserver = object : DefaultLifecycleObserver {

                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    if (owner is Fragment) {
                        resultLauncher = owner.registerForActivityResult(
                            ActivityResultContracts.StartActivityForResult(),
                            activityResult
                        )
                    } else if (owner is ComponentActivity) {
                        resultLauncher = owner.registerForActivityResult(
                            ActivityResultContracts.StartActivityForResult(), activityResult
                        )
                    }
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    resultLauncher = null
                    launchObserver?.apply {
                        lifecycleOwner.lifecycle.removeObserver(this)
                    }

                }
            }
            launchObserver?.apply { lifecycleOwner.lifecycle.addObserver(this) }
        }
    }

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

    fun launchForResult(args: ArgsType) {
        resultLauncher?.apply {
            launchRequestCode = requestCode
            val intent = Intent(originalActivity, targetActivity).putExtra(Args.AIRWALLEX_EXTRA, args)
            intent.putExtra("pageCode", requestCode)
            launch(intent)
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
