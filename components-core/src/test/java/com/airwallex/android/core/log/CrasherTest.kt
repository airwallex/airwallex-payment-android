package com.airwallex.android.core.log

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CrasherTest {

    private lateinit var defaultHandler: Thread.UncaughtExceptionHandler

    @Before
    fun setUp() {
        mockkObject(AirwallexLogger)
        mockkObject(AnalyticsLogger)
        every { AirwallexLogger.info(any()) } returns Unit
        every { AirwallexLogger.error(any<String>(), any()) } returns Unit
        every { AnalyticsLogger.logError(any<String>(), any<Map<String, Any>>()) } returns Unit

        defaultHandler = io.mockk.mockk(relaxed = true)
        // Inject our mock as the default handler that Crasher delegates to.
        val field = Crasher::class.java.getDeclaredField("defaultHandler").apply { isAccessible = true }
        field.set(Crasher, defaultHandler)
        val initField = Crasher::class.java.getDeclaredField("initialized").apply { isAccessible = true }
        initField.setBoolean(Crasher, true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `originatedInSdk returns true when top frame is SDK class`() {
        val throwable = throwableWithTopClass("com.airwallex.android.core.Foo")
        assertTrue(invokeOriginatedInSdk(throwable))
    }

    @Test
    fun `originatedInSdk returns false when top frame is non-SDK class`() {
        val throwable = throwableWithTopClass("com.merchant.app.Bar")
        assertFalse(invokeOriginatedInSdk(throwable))
    }

    @Test
    fun `originatedInSdk walks cause chain to root`() {
        val rootCause = throwableWithTopClass("com.airwallex.android.card.Baz")
        val middle = RuntimeException("middle", rootCause)
        val outer = RuntimeException("outer", middle)
        assertTrue(invokeOriginatedInSdk(outer))
    }

    @Test
    fun `originatedInSdk handles cyclic cause chain without infinite loop`() {
        val a = MutableCauseThrowable("a").apply {
            stackTrace = arrayOf(StackTraceElement("com.airwallex.android.core.Cycle", "m", "F.kt", 1))
        }
        val b = MutableCauseThrowable("b")
        a.mutableCause = b
        b.mutableCause = a
        // Reaching this assertion proves the walk terminated.
        invokeOriginatedInSdk(a)
    }

    @Test
    fun `originatedInSdk returns false when stack trace is empty`() {
        val throwable = RuntimeException("empty").apply { stackTrace = emptyArray() }
        assertFalse(invokeOriginatedInSdk(throwable))
    }

    @Test
    fun `uncaughtException passes through non-SDK exceptions without logging`() {
        val thread = Thread.currentThread()
        val throwable = throwableWithTopClass("com.merchant.app.Bar")

        Crasher.uncaughtException(thread, throwable)

        verify(exactly = 1) { defaultHandler.uncaughtException(thread, throwable) }
        verify(exactly = 0) { AnalyticsLogger.logError(any<String>(), any<Map<String, Any>>()) }
    }

    @Test
    fun `initialize is idempotent`() {
        val before = Thread.getDefaultUncaughtExceptionHandler()
        Crasher.initialize()
        assertEquals(before, Thread.getDefaultUncaughtExceptionHandler())
    }

    private fun throwableWithTopClass(className: String, message: String = "boom"): Throwable {
        return RuntimeException(message).apply {
            stackTrace = arrayOf(StackTraceElement(className, "method", "File.kt", 10))
        }
    }

    private fun invokeOriginatedInSdk(throwable: Throwable): Boolean {
        val method = Crasher::class.java.getDeclaredMethod("originatedInSdk", Throwable::class.java)
            .apply { isAccessible = true }
        return method.invoke(Crasher, throwable) as Boolean
    }

    private open class MutableCauseThrowable(message: String) : RuntimeException(message) {
        var mutableCause: Throwable? = null
        override val cause: Throwable?
            get() = mutableCause
    }
}
