package com.airwallex.android.core

interface AirwallexCallback<T> {
    fun onSuccess(result: T)
    fun onFailure(error: Throwable?)
}