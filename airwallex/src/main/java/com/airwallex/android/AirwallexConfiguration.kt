package com.airwallex.android

import android.content.Context

data class AirwallexConfiguration constructor(val builder: Builder) {

    val enableLogging: Boolean = builder.enableLogging

    class Builder(context: Context) {

        init {
            ContextProvider.init(context)
        }

        var enableLogging: Boolean = false

        fun enableLogging(enable: Boolean): Builder = apply {
            this.enableLogging = enable
        }

        fun build(): AirwallexConfiguration {
            return AirwallexConfiguration(this)
        }
    }
}