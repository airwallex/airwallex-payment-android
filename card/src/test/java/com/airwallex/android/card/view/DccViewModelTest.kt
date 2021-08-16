package com.airwallex.android.card.view

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.model.ContinuePaymentIntentParams
import com.airwallex.android.core.model.PaymentIntentContinueType
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.Error
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class DccViewModelTest {

    private val application = ApplicationProvider.getApplicationContext<Application>()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val threeDSecureViewModel by lazy {
        val activity: Activity = mock()
        DccViewModel(application, Airwallex(activity, context))
    }

    @Test
    fun continuePaymentIntentTest() {
        val countDownLatch = CountDownLatch(1)
        var message: String? = null
        threeDSecureViewModel.continuePaymentIntent(
            ContinuePaymentIntentParams(
                paymentIntentId = "1111",
                clientSecret = "222",
                type = PaymentIntentContinueType.DCC
            )
        ).observeForever {
            when (it) {
                is Error -> message = it.message
                else -> Unit
            }

            countDownLatch.countDown()
        }

        countDownLatch.await(3, TimeUnit.MILLISECONDS)
        assertEquals(null, message)
    }
}
