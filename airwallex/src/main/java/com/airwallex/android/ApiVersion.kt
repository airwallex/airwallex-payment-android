package com.airwallex.android

internal data class ApiVersion internal constructor(internal val code: String) {

    override fun toString(): String {
        return code
    }

    internal companion object {
        private const val API_VERSION_CODE: String = "2020-04-22"

        private val INSTANCE = ApiVersion(API_VERSION_CODE)

        @JvmSynthetic
        internal fun get(): ApiVersion {
            return INSTANCE
        }
    }
}
