package com.airwallex.android

import android.util.Log

/**
 * Formatted log for Airwallex
 */
internal class Logger {

    companion object {
        fun getLogWorker(enableLogging: Boolean): LogWorker {
            return if (enableLogging) {
                DEFAULT_LOG_WORKER
            } else {
                NOOP_LOG_WORKER
            }
        }

        private val DEFAULT_LOG_WORKER = object : LogWorker {
            override fun log(level: Level, message: String?, throwable: Throwable?) {
                val description = message ?: "<Missing Description>"
                when (level) {
                    Level.VERBOSE -> Log.v(Level.VERBOSE.name, description, throwable)
                    Level.DEBUG -> Log.d(Level.DEBUG.name, description, throwable)
                    Level.INFO -> Log.i(Level.INFO.name, description, throwable)
                    Level.WARNING -> Log.w(Level.WARNING.name, description, throwable)
                    Level.ERROR -> Log.e(Level.ERROR.name, description, throwable)
                }
            }
        }

        private val NOOP_LOG_WORKER = object : LogWorker {
            override fun log(level: Level, message: String?, throwable: Throwable?) {
                // ignore
            }
        }
    }

    internal enum class Level {
        VERBOSE, DEBUG, INFO, WARNING, ERROR
    }

    internal interface LogWorker {
        fun log(level: Level, message: String? = null, throwable: Throwable? = null)
    }
}
