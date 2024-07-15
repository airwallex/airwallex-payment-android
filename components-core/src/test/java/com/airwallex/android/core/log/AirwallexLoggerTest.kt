package com.airwallex.android.core.log

import android.content.Context
import android.util.Log
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class AirwallexLoggerTest {

    private lateinit var mockContext: Context
    private lateinit var spyLogWorker: AirwallexLogger.LogWorker

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        mockContext = mockk(relaxed = true)
        spyLogWorker = spyk(AirwallexLogger.DEFAULT_LOG_WORKER, recordPrivateCalls = true)
        val logDir = File("mocked/path/to/files")
        every { mockContext.getExternalFilesDir(null) } returns logDir

        AirwallexLogger.initialize(mockContext, loggingEnabled = true, saveLogToLocal = true, spyLogWorker)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
        every { Log.i(any(), any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.v(any(), any(), any()) } returns 0
        every { Log.getStackTraceString(any()) } returns ""
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test error logging calls writeLogToFile`() = runBlocking {
        val message = "Error message"
        val throwable = RuntimeException()

        AirwallexLogger.error(message, throwable)
        coVerify { spyLogWorker["writeLogToFile"](any<String>()) }
    }

    @Test
    fun `test initialize calls clearOldLogs`() = runBlocking {
        AirwallexLogger.initialize(mockContext, loggingEnabled = true, saveLogToLocal = true)
        verify { spyLogWorker["clearOldLogs"](any<Int>()) }
    }

    @Test
    fun `test error logging`() {
        val message = "Error message"
        val throwable = RuntimeException()

        AirwallexLogger.error(message, throwable)

        verify(exactly = 1) {
            Log.e(eq("AirwallexLogger"), withArg { arg -> arg.contains(message) }, eq(throwable))
        }
    }

    @Test
    fun `test warn logging`() {
        val message = "Warning message"
        val throwable = RuntimeException()

        AirwallexLogger.warn(message, throwable)

        verify(exactly = 1) {
            Log.w(eq("AirwallexLogger"), withArg { arg -> arg.contains(message) }, eq(throwable))
        }
    }

    @Test
    fun `test info logging`() {
        val message = "Info message"
        val throwable = RuntimeException()

        AirwallexLogger.info(message, throwable)

        verify(exactly = 1) {
            Log.i(eq("AirwallexLogger"), withArg { arg -> arg.contains(message) }, eq(throwable))
        }
    }

    @Test
    fun `test info logging with parameter sensitiveMessage`() {
        val message = "Info message"
        val sensitiveMessage = "Sensitive message"
        val throwable = RuntimeException()

        AirwallexLogger.info(message, throwable, sensitiveMessage)

        verify(exactly = 1) {
            Log.i(
                eq("AirwallexLogger"),
                withArg { arg -> arg.contains(message) && arg.contains(sensitiveMessage) },
                eq(throwable)
            )
        }
    }

    @Test
    fun `test debug logging`() {
        val message = "Debug message"
        val throwable = RuntimeException()

        AirwallexLogger.debug(message, throwable)

        verify(exactly = 1) {
            Log.d(eq("AirwallexLogger"), withArg { arg -> arg.contains(message) }, eq(throwable))
        }
    }

    @Test
    fun `test verbose logging`() {
        val message = "Verbose message"
        val throwable = RuntimeException()

        AirwallexLogger.verbose(message, throwable)

        verify(exactly = 1) {
            Log.v(eq("AirwallexLogger"), withArg { arg -> arg.contains(message) }, eq(throwable))
        }
    }

    @Test
    fun `test error logging with loggingEnabled false`() {
        val message = "Error message"
        val throwable = RuntimeException()

        AirwallexLogger.initialize(mockContext, loggingEnabled = false)

        AirwallexLogger.error(message, throwable)

        verify(exactly = 0) {
            Log.e(any(), any(), any())
        }
    }

    @Test
    fun `test error logging with saveLogToLocal false`() = runBlocking {
        val message = "Error message"
        val throwable = RuntimeException()

        AirwallexLogger.initialize(mockContext, saveLogToLocal = false)

        AirwallexLogger.error(message, throwable)

        coVerify(inverse = true) { spyLogWorker["writeLogToFile"](any<String>()) }
    }

    @Test
    fun `test warn logging with loggingEnabled false`() {
        val message = "Warning message"
        val throwable = RuntimeException()

        AirwallexLogger.initialize(mockContext, loggingEnabled = false)

        AirwallexLogger.warn(message, throwable)

        verify(exactly = 0) {
            Log.w(any(), any(), any())
        }
    }

    @Test
    fun `test warn logging with saveLogToLocal false`() = runBlocking {
        val message = "Warning message"
        val throwable = RuntimeException()

        AirwallexLogger.initialize(mockContext, saveLogToLocal = false)

        AirwallexLogger.warn(message, throwable)

        coVerify(inverse = true) { spyLogWorker["writeLogToFile"](any<String>()) }
    }

    @Test
    fun `test info logging with loggingEnabled false`() {
        val message = "Info message"
        val throwable = RuntimeException()

        AirwallexLogger.initialize(mockContext, loggingEnabled = false)
        AirwallexLogger.info(message, throwable)
        verify(exactly = 0) {
            Log.i(any(), any(), any())
        }
    }

    @Test
    fun `test info logging with saveLogToLocal false`() = runBlocking {
        val message = "Info message"
        val throwable = RuntimeException()

        AirwallexLogger.initialize(mockContext, saveLogToLocal = false)
        AirwallexLogger.info(message, throwable)
        coVerify(inverse = true) { spyLogWorker["writeLogToFile"](any<String>()) }
    }

    // Repeat similar tests for debug and verbose log levels:
    @Test
    fun `test debug logging with loggingEnabled false`() {
        val message = "Debug message"
        val throwable = RuntimeException()

        AirwallexLogger.initialize(mockContext, loggingEnabled = false)
        AirwallexLogger.debug(message, throwable)
        verify(exactly = 0) {
            Log.d(any(), any(), any())
        }
    }

    @Test
    fun `test debug logging with saveLogToLocal false`() = runBlocking {
        val message = "Debug message"
        val throwable = RuntimeException()

        AirwallexLogger.initialize(mockContext, saveLogToLocal = false)
        AirwallexLogger.debug(message, throwable)
        coVerify(inverse = true) { spyLogWorker["writeLogToFile"](any<String>()) }
    }

    @Test
    fun `test verbose logging with loggingEnabled false`() {
        val message = "Verbose message"
        val throwable = RuntimeException()

        AirwallexLogger.initialize(mockContext, loggingEnabled = false)
        AirwallexLogger.verbose(message, throwable)

        verify(exactly = 0) {
            Log.v(any(), any(), any())
        }
    }

    @Test
    fun `test verbose logging with saveLogToLocal false`() = runBlocking {
        val message = "Verbose message"
        val throwable = RuntimeException()

        AirwallexLogger.initialize(mockContext, saveLogToLocal = false)
        AirwallexLogger.verbose(message, throwable)

        coVerify(inverse = true) { spyLogWorker["writeLogToFile"](any<String>()) }
    }
}