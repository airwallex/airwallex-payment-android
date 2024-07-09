package com.airwallex.android.core.log

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Formatted log for Airwallex
 */
object AirwallexLogger {
    private const val LOG_TAG = "AirwallexLogger"
    private var loggingEnabled: Boolean = false
    private var saveLogToLocal: Boolean = false

    fun initialize(
        context: Context,
        loggingEnabled: Boolean = false,
        saveLogToLocal: Boolean = false
    ) {
        this.loggingEnabled = loggingEnabled
        this.saveLogToLocal = saveLogToLocal
        logWorker.initialize(context)
    }


    @JvmField
    internal val DEFAULT_LOG_WORKER = object : LogWorker {
        private var logFile: File? = null
        private val ioScope = CoroutineScope(Dispatchers.IO)
        private val LOG_DIR = "AirwallexLogger"
        private val LOG_FILE_NAME = "log.txt"

        override fun initialize(context: Context) {
            if (saveLogToLocal) {
                val logDir = File(context.getExternalFilesDir(null), LOG_DIR)
                if (!logDir.exists()) {
                    logDir.mkdirs()
                }
                logFile = File(logDir, LOG_FILE_NAME)
                clearOldLogs(7)
            }
        }

        override fun log(level: Level, message: String?, throwable: Throwable?) {
            val timestamp = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(Date())
            val logMessage = "$timestamp $message\n"
            if (loggingEnabled) {
                when (level) {
                    Level.VERBOSE -> Log.v(LOG_TAG, logMessage, throwable)
                    Level.DEBUG -> Log.d(LOG_TAG, logMessage, throwable)
                    Level.INFO -> Log.i(LOG_TAG, logMessage, throwable)
                    Level.WARNING -> Log.w(LOG_TAG, logMessage, throwable)
                    Level.ERROR -> Log.e(LOG_TAG, logMessage, throwable)
                }
            }
            if (saveLogToLocal) {
                ioScope.launch {
                    writeLogToFile("$logMessage ${Log.getStackTraceString(throwable)}")
                }
            }
        }

        private suspend fun writeLogToFile(logMessage: String) {
            logFile?.let {
                withContext(Dispatchers.IO) {
                    try {
                        val writer = FileWriter(it, true)
                        writer.append(logMessage)
                        writer.flush()
                        writer.close()
                    } catch (e: IOException) {
                        Log.e(LOG_TAG, "Failed to write log to file", e)
                    }
                }
            }
        }

        private fun clearOldLogs(retentionDays: Int = 7) {
            ioScope.launch {
                logFile?.let {
                    if (it.exists()) {
                        val lastModified = it.lastModified()
                        val currentTime = System.currentTimeMillis()
                        val ageInDays = (currentTime - lastModified) / (1000 * 60 * 60 * 24)
                        if (ageInDays > retentionDays) {
                            it.delete()
                        }
                    }
                }
            }
        }
    }

    private var logWorker: LogWorker = DEFAULT_LOG_WORKER

    fun error(message: String?, throwable: Throwable? = null) {
        logWorker.log(Level.ERROR, message, throwable)
    }

    fun warn(message: String?, throwable: Throwable? = null) {
        logWorker.log(Level.WARNING, message, throwable)
    }

    fun info(message: String?, throwable: Throwable? = null) {
        logWorker.log(Level.INFO, message, throwable)
    }

    fun debug(message: String?, throwable: Throwable? = null) {
        logWorker.log(Level.DEBUG, message, throwable)
    }

    fun verbose(message: String?, throwable: Throwable? = null) {
        logWorker.log(Level.VERBOSE, message, throwable)
    }

    internal enum class Level {
        VERBOSE, DEBUG, INFO, WARNING, ERROR
    }

    internal interface LogWorker {
        fun log(level: Level, message: String?, throwable: Throwable?)

        fun initialize(context: Context)
    }
}
