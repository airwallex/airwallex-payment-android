package com.airwallex.android.core.util

import com.airwallex.android.core.AirwallexCallback
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AirwallexCallbackTest {

    private lateinit var callback: AirwallexCallback<String>

    @Before
    fun setUp() {
        callback = mockk()
    }

    @Test
    fun testOnSuccess() = runTest {
        every { callback.onSuccess(any()) } just Runs
        callback.onSuccess("Test success")
        verify { callback.onSuccess("Test success") }
        confirmVerified(callback)
    }

    @Test
    fun testOnFailure() = runTest {
        every { callback.onFailure(any()) } just Runs
        val sampleError = Exception("Test error")
        callback.onFailure(sampleError)
        verify { callback.onFailure(sampleError) }
        confirmVerified(callback)
    }
}