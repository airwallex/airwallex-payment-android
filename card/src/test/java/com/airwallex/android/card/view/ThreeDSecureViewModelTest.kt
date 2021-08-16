package com.airwallex.android.card.view

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ThreeDSecureViewModelTest {

    private val application = ApplicationProvider.getApplicationContext<Application>()

    private val threeDSecureViewModel by lazy {
        ThreeDSecureViewModel(application)
    }

    @Test
    fun continueThreeDSecureTest() {
        val activity: Activity = mock()

        val countDownLatch = CountDownLatch(1)
        var jwt: String? = null
        threeDSecureViewModel.continueThreeDSecure("11", "222", activity).observeForever {
            when (it) {
                is ThreeDSecureViewModel.ThreeDSecureResult.Complete -> jwt = it.jwt
                else -> Unit
            }

            countDownLatch.countDown()
        }

        countDownLatch.await()
        assertEquals("", jwt)
    }
}
