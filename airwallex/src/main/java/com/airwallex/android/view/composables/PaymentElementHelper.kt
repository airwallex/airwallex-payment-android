package com.airwallex.android.view.composables

import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.view.PaymentFlowListener
import kotlinx.coroutines.launch

/**
 * Java-friendly helper for creating and rendering PaymentElement.
 *
 * This class provides a simplified API for Java developers to use PaymentElement
 * without dealing with Kotlin suspend functions and Composable rendering.
 *
 * Example usage from Java:
 * ```java
 * PaymentElementHelper.create(
 *     this,  // ComponentActivity
 *     session,
 *     airwallex,
 *     configuration,
 *     listener,
 *     composeView,
 *     progressBar,
 *     new PaymentElementCallback() {
 *         @Override
 *         public void onSuccess(PaymentElement element) {
 *             // Element created successfully and already rendered in composeView
 *         }
 *
 *         @Override
 *         public void onFailure(Throwable error) {
 *             // Handle error
 *         }
 *     }
 * );
 * ```
 */
object PaymentElementHelper {

    /**
     * Creates a PaymentElement and automatically renders it in the provided ComposeView.
     *
     * This method handles:
     * - Calling the suspend create() function
     * - Managing loading states (showing/hiding progressBar)
     * - Rendering the Composable UI in the ComposeView
     * - Calling the appropriate callback on success or failure
     *
     * @param activity The ComponentActivity (required for lifecycle scope)
     * @param session The Airwallex session containing payment information
     * @param airwallex The Airwallex instance for payment operations
     * @param configuration Configuration for the payment element
     * @param paymentFlowListener Listener for payment operation callbacks
     * @param composeView The ComposeView where the payment UI will be rendered
     * @param progressBar Optional progress bar to show/hide during creation
     * @param callback Callback to receive the result
     */
    @Suppress("LongParameterList")
    @JvmStatic
    fun create(
        activity: ComponentActivity,
        session: AirwallexSession,
        airwallex: Airwallex,
        configuration: PaymentElementConfiguration,
        paymentFlowListener: PaymentFlowListener,
        composeView: ComposeView,
        progressBar: View?,
        callback: PaymentElementCallback
    ) {
        // Show loading
        progressBar?.visibility = View.VISIBLE
        composeView.visibility = View.GONE

        // Launch coroutine in activity's lifecycle scope
        activity.lifecycleScope.launch {
            val result = PaymentElement.create(
                session = session,
                airwallex = airwallex,
                configuration = configuration,
                paymentFlowListener = paymentFlowListener
            )

            result.onSuccess { paymentElement ->
                // Hide loading
                progressBar?.visibility = View.GONE
                composeView.visibility = View.VISIBLE

                // Render the payment UI
                composeView.setContent {
                    paymentElement.Content()
                }

                // Notify success
                callback.onSuccess(paymentElement)
            }.onFailure { throwable ->
                // Hide loading
                progressBar?.visibility = View.GONE

                // Notify failure
                callback.onFailure(throwable)
            }
        }
    }

    /**
     * Creates a PaymentElement without automatic rendering.
     *
     * Use this if you want to handle the rendering yourself or need more control
     * over when and how the element is rendered.
     *
     * @param activity The ComponentActivity (required for lifecycle scope)
     * @param session The Airwallex session containing payment information
     * @param airwallex The Airwallex instance for payment operations
     * @param configuration Configuration for the payment element
     * @param paymentFlowListener Listener for payment operation callbacks
     * @param callback Callback to receive the result
     */
    @Suppress("LongParameterList")
    @JvmStatic
    fun createOnly(
        activity: ComponentActivity,
        session: AirwallexSession,
        airwallex: Airwallex,
        configuration: PaymentElementConfiguration,
        paymentFlowListener: PaymentFlowListener,
        callback: PaymentElementCallback
    ) {
        activity.lifecycleScope.launch {
            val result = PaymentElement.create(
                session = session,
                airwallex = airwallex,
                configuration = configuration,
                paymentFlowListener = paymentFlowListener
            )

            result.onSuccess { paymentElement ->
                callback.onSuccess(paymentElement)
            }.onFailure { throwable ->
                callback.onFailure(throwable)
            }
        }
    }

    /**
     * Renders a PaymentElement in the given ComposeView.
     *
     * Call this method if you used [createOnly] and want to render the element later.
     *
     * @param paymentElement The PaymentElement to render
     * @param composeView The ComposeView where the payment UI will be rendered
     */
    @JvmStatic
    fun renderInView(paymentElement: PaymentElement, composeView: ComposeView) {
        composeView.setContent {
            paymentElement.Content()
        }
    }
}

/**
 * Callback interface for PaymentElement creation results.
 *
 * Implement this interface to receive callbacks when PaymentElement creation
 * succeeds or fails.
 */
interface PaymentElementCallback {
    /**
     * Called when PaymentElement is successfully created.
     *
     * @param element The created PaymentElement
     */
    fun onSuccess(element: PaymentElement)

    /**
     * Called when PaymentElement creation fails.
     *
     * @param error The error that occurred
     */
    fun onFailure(error: Throwable)
}
