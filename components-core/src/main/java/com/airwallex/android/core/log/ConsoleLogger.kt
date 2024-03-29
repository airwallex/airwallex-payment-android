package com.airwallex.android.core.log

import android.util.Log
import com.airwallex.android.core.AirwallexPlugins

/**
 * Formatted log for Airwallex
 */
object ConsoleLogger {

    private val loggingEnabled: Boolean
        get() {
            return AirwallexPlugins.enableLogging
        }

    internal enum class Level {
        VERBOSE, DEBUG, INFO, WARNING, ERROR
    }

    internal interface LogWorker {
        fun log(level: Level, tag: String, message: String?, throwable: Throwable?)
    }

    @JvmField
    internal val DEFAULT_LOG_WORKER = object : LogWorker {
        override fun log(level: Level, tag: String, message: String?, throwable: Throwable?) {
            val description = message ?: "<Missing Description>"
            when (level) {
                Level.VERBOSE -> Log.v(tag, description, throwable)
                Level.DEBUG -> Log.d(tag, description, throwable)
                Level.INFO -> Log.i(tag, description, throwable)
                Level.WARNING -> Log.w(tag, description, throwable)
                Level.ERROR -> Log.e(tag, description, throwable)
            }
        }
    }

    private var logWorker: LogWorker =
        DEFAULT_LOG_WORKER

    fun error(message: String?, throwable: Throwable? = null) =
        error("ERROR", message, throwable)

    fun error(tag: String, message: String?, throwable: Throwable? = null) {
        if (loggingEnabled) {
            logWorker.log(
                Level.ERROR, tag, message, throwable
            )
        }
    }

    fun warn(tag: String, message: String?, throwable: Throwable? = null) {
        if (loggingEnabled) {
            logWorker.log(
                Level.WARNING, tag, message, throwable
            )
        }
    }

    fun warn(message: String?, throwable: Throwable? = null) =
        warn("WARN", message, throwable)

    fun info(message: String?, throwable: Throwable? = null) =
        info("INFO", message, throwable)

    fun info(tag: String, message: String?, throwable: Throwable? = null) {
        if (loggingEnabled) {
            logWorker.log(
                Level.INFO, tag, message, throwable
            )
        }
    }

    fun debug(message: String?, throwable: Throwable? = null) =
        debug("DEBUG", message, throwable)

    fun debug(tag: String, message: String?, throwable: Throwable? = null) {
        if (loggingEnabled) {
            logWorker.log(
                Level.DEBUG, tag, message, throwable
            )
        }
    }

    fun verbose(message: String?, throwable: Throwable? = null) =
        verbose("VERBOSE", message, throwable)

    fun verbose(tag: String, message: String?, throwable: Throwable? = null) {
        if (loggingEnabled) {
            logWorker.log(
                Level.VERBOSE, tag, message, throwable
            )
        }
    }
}
