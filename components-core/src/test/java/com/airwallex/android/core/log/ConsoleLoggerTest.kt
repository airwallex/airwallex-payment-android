package com.airwallex.android.core.log

import android.util.Log
import com.airwallex.android.core.AirwallexPlugins
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class ConsoleLoggerTest {
    @Before
    fun setUp() {
        mockkStatic(Log::class)
        mockkObject(AirwallexPlugins)

        every { AirwallexPlugins.enableLogging } returns true
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.w(any(), any(), any()) } returns 0
        every { Log.i(any(), any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.v(any(), any(), any()) } returns 0
    }

    @After
    fun unmock() {
        unmockkAll()
    }

    @Test
    fun `test error logging`() {
        val message = "Error message"
        val throwable = RuntimeException()

        ConsoleLogger.error(message, throwable)

        verify(exactly = 1) {
            Log.e("ERROR", message, throwable)
        }
    }

    @Test
    fun `test warn logging`() {
        val message = "Warning message"
        val throwable = RuntimeException()

        ConsoleLogger.warn(message, throwable)

        verify(exactly = 1) {
            Log.w("WARN", message, throwable)
        }
    }

    @Test
    fun `test info logging`() {
        val message = "Info message"
        val throwable = RuntimeException()

        ConsoleLogger.info(message, throwable)

        verify(exactly = 1) {
            Log.i("INFO", message, throwable)
        }
    }

    @Test
    fun `test debug logging`() {
        val message = "Debug message"
        val throwable = RuntimeException()

        ConsoleLogger.debug(message, throwable)

        verify(exactly = 1) {
            Log.d("DEBUG", message, throwable)
        }
    }

    @Test
    fun `test verbose logging`() {
        val message = "Verbose message"
        val throwable = RuntimeException()

        ConsoleLogger.verbose(message, throwable)

        verify(exactly = 1) {
            Log.v("VERBOSE", message, throwable)
        }
    }
}