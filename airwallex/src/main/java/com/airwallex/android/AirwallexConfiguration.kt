package com.airwallex.android

import android.content.Context

data class AirwallexConfiguration internal constructor(
    val enableLogging: Boolean
) {

    class Builder(context: Context) {

        init {
            ContextProvider.init(context)
        }

        var enableLogging: Boolean = false

        fun enableLogging(enable: Boolean): Builder = apply {
            this.enableLogging = enable
        }

        fun build(): AirwallexConfiguration {
            return AirwallexConfiguration(
                enableLogging = enableLogging
            )
        }
    }
}