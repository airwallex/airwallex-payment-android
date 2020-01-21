package com.airwallex.android

import com.airwallex.android.exception.AirwallexException

internal data class ResultWrapper<ResultType> internal constructor(
    val result: ResultType? = null,
    val error: AirwallexException? = null
) {
    internal companion object {
        @JvmSynthetic
        internal fun <ResultType> create(result: ResultType?): ResultWrapper<ResultType> {
            return ResultWrapper(result = result)
        }

        @JvmSynthetic
        internal fun <ResultType> create(error: AirwallexException): ResultWrapper<ResultType> {
            return ResultWrapper(error = error)
        }
    }
}
