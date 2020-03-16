package com.airwallex.android

import android.util.Log

internal object Logger {

    private var loggingEnabled: Boolean = BuildConfig.DEBUG

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

    internal fun error(message: String?, throwable: Throwable? = null) =
        error("ERROR", message, throwable)

    internal fun error(tag: String, message: String?, throwable: Throwable? = null) {
        if (loggingEnabled) logWorker.log(
            Level.ERROR, tag, message, throwable
        )
    }

    internal fun warn(tag: String, message: String?, throwable: Throwable? = null) {
        if (loggingEnabled) logWorker.log(
            Level.WARNING, tag, message, throwable
        )
    }

    internal fun warn(message: String?, throwable: Throwable? = null) =
        warn("WARN", message, throwable)

    internal fun info(message: String?, throwable: Throwable? = null) =
        info("INFO", message, throwable)

    internal fun info(tag: String, message: String?, throwable: Throwable? = null) {
        if (loggingEnabled) logWorker.log(
            Level.INFO, tag, message, throwable
        )
    }

    internal fun debug(message: String?, throwable: Throwable? = null) =
        debug("DEBUG", message, throwable)

    internal fun debug(tag: String, message: String?, throwable: Throwable? = null) {
        if (loggingEnabled) logWorker.log(
            Level.DEBUG, tag, message, throwable
        )
    }

    internal fun verbose(message: String?, throwable: Throwable? = null) =
        verbose("VERBOSE", message, throwable)

    internal fun verbose(tag: String, message: String?, throwable: Throwable? = null) {
        if (loggingEnabled) logWorker.log(
            Level.VERBOSE, tag, message, throwable
        )
    }
}
