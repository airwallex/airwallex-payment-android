package com.airwallex.android

import com.airwallex.android.exception.AirwallexException

internal data class ResultWrapper<Result> internal constructor(
    val result: Result? = null,
    val exception: AirwallexException? = null
) {
    internal companion object {
        @JvmSynthetic
        internal fun <Result> create(result: Result?): ResultWrapper<Result> {
            return ResultWrapper(result = result)
        }

        @JvmSynthetic
        internal fun <Result> create(error: AirwallexException): ResultWrapper<Result> {
            return ResultWrapper(exception = error)
        }
    }
}
