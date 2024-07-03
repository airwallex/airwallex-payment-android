package com.airwallex.android.ui

import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback

abstract class AirwallexActivityLaunchResultCallback : ActivityResultCallback<ActivityResult> {

    private var callback: ((requestCode: Int, result: ActivityResult) -> Unit)? = null
    private var requestCode: Int? = null

    fun setResultCallback(callback: (requestCode: Int, result: ActivityResult) -> Unit) {
        this.callback = callback
    }

    fun getResultCallback(): ((requestCode: Int, result: ActivityResult) -> Unit)? = callback

    fun setRequestCode(requestCode: Int) {
        this.requestCode = requestCode
    }

    fun getRequestCode(): Int? = requestCode
}