package com.airwallex.android.core.log

import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

/**
 * Uncaught exception handler that logs crashes originating inside the Airwallex SDK
 * and forwards the exception to the previously installed default handler.
 *
 * Crashes whose root cause originated outside the SDK (e.g. in the host app) are
 * passed through untouched so we don't pollute analytics with merchant-side bugs.
 */
object Crasher : Thread.UncaughtExceptionHandler {

    private const val SDK_PACKAGE_PREFIX = "com.airwallex.android."
    private const val EXIT_DELAY_MILLIS = 1000L

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null
    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        if (originatedInSdk(throwable)) {
            val errorMessage = throwable.message ?: "No error message available"
            AirwallexLogger.info("==================Crash detected==================")
            AirwallexLogger.error("Crash thread: ${thread.name}")
            AirwallexLogger.error("Crash message: $errorMessage")
            AirwallexLogger.error(getStackTrace(throwable))
            AnalyticsLogger.logError("crash", mapOf("message" to errorMessage))

            // Give the IO log writer time to flush before the process is killed.
            try {
                Thread.sleep(EXIT_DELAY_MILLIS)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            defaultHandler?.uncaughtException(thread, throwable)
            exitProcess(2)
        } else {
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun originatedInSdk(throwable: Throwable): Boolean {
        var rootCause: Throwable = throwable
        val seen = mutableSetOf(rootCause)
        while (true) {
            val next = rootCause.cause ?: break
            if (!seen.add(next)) break
            rootCause = next
        }
        val topClassName = rootCause.stackTrace.firstOrNull()?.className ?: return false
        return topClassName.startsWith(SDK_PACKAGE_PREFIX)
    }

    private fun getStackTrace(throwable: Throwable): String {
        val stringWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stringWriter))
        return stringWriter.toString()
    }
}
