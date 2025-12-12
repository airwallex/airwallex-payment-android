package com.airwallex.paymentacceptance.util

import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.model.RetrievePaymentIntentParams
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.min
import kotlin.math.pow

/**
 * Polls payment status with exponential backoff strategy.
 * Mirrors iOS PaymentStatusPoller implementation.
 *
 * Features:
 * - Exponential backoff: 2s → 4s → 8s → 16s (max)
 * - Maximum polling duration: 5 minutes
 * - Final status detection
 * - Lifecycle-aware: waits when app goes to background
 */
class PaymentStatusPoller(
    private val intentId: String,
    private val clientSecret: String,
    private val airwallex: Airwallex,
    private val maxPollingDuration: Long = 300_000L, // 5 minutes
    private val baseInterval: Long = 2000L,
    private val maxInterval: Long = 16_000L
) {

    companion object {
        private const val TAG = "PaymentStatusPoller"
    }

    private var pollingJob: Job? = null

    sealed class PollingResult {
        data class Complete(val description: String) : PollingResult()
        data class Timeout(val description: String) : PollingResult()
        data class Error(val message: String) : PollingResult()
        data object PaymentAttemptNotFound : PollingResult()
    }

    /**
     * Poll for payment status until final state or timeout.
     * This is a suspend function that returns the final result.
     */
    suspend fun getPaymentAttempt(): PollingResult {
        pollingJob = currentCoroutineContext().job
        val startTime = System.currentTimeMillis()
        var attempts = 0

        debugLog("Starting polling for intent: $intentId")

        while (true) {
            // Wait for app to be active
            while (!isAppInForeground()) {
                debugLog("App not active, waiting...")
                delay(1000)
            }

            // Fetch status
            debugLog("Poll attempt #${attempts + 1}")

            val result = try {
                retrievePaymentIntent()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                debugLog("API error: ${e.localizedMessage}")
                return PollingResult.Error(e.localizedMessage ?: "Unknown error")
            }

            val paymentAttempt = result.latestPaymentAttempt
            if (paymentAttempt == null) {
                debugLog("Payment attempt not found")
                return PollingResult.PaymentAttemptNotFound
            }

            debugLog("Payment attempt status: ${paymentAttempt.status}")

            if (paymentAttempt.isFinal()) {
                debugLog("Final status reached: ${paymentAttempt.status}")
                return PollingResult.Complete(paymentAttempt.getDescription())
            }

            // Check timeout
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed >= maxPollingDuration) {
                debugLog("Timeout after ${elapsed}ms, last status: ${paymentAttempt.status}")
                return PollingResult.Timeout(paymentAttempt.getDescription())
            }

            // Wait before next poll (exponential backoff)
            val interval = min(baseInterval * 2.0.pow(attempts.toDouble()).toLong(), maxInterval)
            attempts++
            debugLog("Waiting ${interval}ms before next poll...")
            delay(interval)
        }
    }

    /**
     * Stop polling
     */
    fun stop() {
        debugLog("Polling stopped")
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun isAppInForeground(): Boolean {
        return ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(
            androidx.lifecycle.Lifecycle.State.STARTED
        )
    }

    private suspend fun retrievePaymentIntent() = suspendCancellableCoroutine { continuation ->
        val params = RetrievePaymentIntentParams(
            paymentIntentId = intentId,
            clientSecret = clientSecret
        )

        airwallex.retrievePaymentIntent(
            params = params,
            listener = object : Airwallex.PaymentListener<com.airwallex.android.core.model.PaymentIntent> {
                override fun onSuccess(response: com.airwallex.android.core.model.PaymentIntent) {
                    if (continuation.isActive) {
                        continuation.resume(response)
                    }
                }

                override fun onFailed(exception: com.airwallex.android.core.exception.AirwallexException) {
                    if (continuation.isActive) {
                        continuation.resumeWith(Result.failure(exception))
                    }
                }
            }
        )
    }

    private fun debugLog(message: String) {
        Log.d(TAG, message)
    }
}
