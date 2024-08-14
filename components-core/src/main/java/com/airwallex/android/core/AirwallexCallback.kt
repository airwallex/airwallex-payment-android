package com.airwallex.android.core

import com.airwallex.android.core.exception.AirwallexException

interface AirwallexCallback<T> {
    fun onSuccess(result: T)
    fun onFailure(error: AirwallexException?)
}