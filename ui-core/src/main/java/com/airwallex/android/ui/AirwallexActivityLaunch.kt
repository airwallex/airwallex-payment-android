package com.airwallex.android.ui

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

abstract class AirwallexActivityLaunch<TargetActivity : Activity, ArgsType : AirwallexActivityLaunch.Args> constructor(
    private val originalActivity: Activity,
    private val fragment: Fragment? = null,
    private val targetActivity: Class<TargetActivity>,
    private val requestCode: Int
) {

    companion object {
        private var isInitialized = false
        private val resultLauncherMap = HashMap<Activity, ActivityResultLauncher<Intent>>()
        private val resultCallbackMap = HashMap<Activity, AirwallexActivityLaunchResultCallback>()
        private val launchInstanceMap = HashMap<Class<*>, AirwallexActivityLaunch<*, *>>()

        fun initialize(application: Application) {
            if (isInitialized) return
            isInitialized = true
            registerAllActivityResult(application)
        }

        private fun registerAllActivityResult(application: Application) {
            application.registerActivityLifecycleCallbacks(object :
                AirwallexActivityLaunchLifecycleCallbacks() {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    if (activity is ComponentActivity) {
                        val resultCallback = object : AirwallexActivityLaunchResultCallback() {
                            override fun onActivityResult(result: ActivityResult) {
                                getRequestCode()?.let {
                                    getResultCallback()?.invoke(it, result)
                                }
                            }
                        }
                        val resultLauncher = activity.registerForActivityResult(
                            ActivityResultContracts.StartActivityForResult(),
                            resultCallback
                        )
                        resultLauncherMap[activity] = resultLauncher
                        resultCallbackMap[activity] = resultCallback
                        launchInstanceMap[activity.javaClass]?.onTargetActivityCreated(activity)
                    }
                }

                override fun onActivityDestroyed(activity: Activity) {
                    resultLauncherMap.remove(activity)
                    resultCallbackMap.remove(activity)
                    launchInstanceMap.remove(activity.javaClass)
                }
            })
        }

        private fun setResultCallBack(
            activity: Activity,
            requestCode: Int,
            resultCallBack: (requestCode: Int, result: ActivityResult) -> Unit
        ) {
            resultCallbackMap[activity]?.setRequestCode(requestCode)
            resultCallbackMap[activity]?.setResultCallback(resultCallBack)
        }

        private fun getActivityResultLauncher(activity: Activity): ActivityResultLauncher<Intent>? =
            resultLauncherMap[activity]
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

    init {
        this.also { launcher -> launchInstanceMap[targetActivity] = launcher }
    }

    fun startForResult(args: ArgsType) {
        val intent = Intent(originalActivity, targetActivity).putExtra(Args.AIRWALLEX_EXTRA, args)
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode)
        } else {
            originalActivity.startActivityForResult(intent, requestCode)
        }
    }

    fun launchForResult(
        args: ArgsType,
        resultCallBack: (requestCode: Int, result: ActivityResult) -> Unit
    ) {
        val intent = Intent(originalActivity, targetActivity).putExtra(Args.AIRWALLEX_EXTRA, args)
        setResultCallBack(originalActivity, requestCode, resultCallBack)
        getActivityResultLauncher(originalActivity)?.launch(intent)
    }

    open fun onTargetActivityCreated(target: Activity) {

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
