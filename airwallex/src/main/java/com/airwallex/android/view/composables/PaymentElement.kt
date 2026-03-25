package com.airwallex.android.view.composables

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.bindToActivity
import com.airwallex.android.core.exception.InvalidParamsException
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.AnalyticsLogger.Field
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.toAnalyticsLayoutString
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.PaymentFlowViewModel
import com.airwallex.android.view.util.AnalyticsConstants
import kotlinx.coroutines.launch

class PaymentElement private constructor(
    private val session: AirwallexSession,
    private val airwallex: Airwallex,
    private val configuration: PaymentElementConfiguration,
    private val flowViewModel: PaymentFlowViewModel,
    private val paymentFlowListener: PaymentFlowListener
) {

    companion object {
        /**
         * Creates an [PaymentElement] by fetching required data.
         *
         * This method obtains a [PaymentFlowViewModel] scoped to the Activity,
         * checks if data is already loaded (for configuration changes), and fetches
         * if necessary.
         *
         * This is the public API that always uses EMBEDDED launch type.
         * For SDK internal usage with custom launch types, use the internal overload.
         *
         * @param session The Airwallex session containing payment information
         * @param airwallex The Airwallex instance for payment operations
         * @param configuration Configuration for the payment element
         * @param paymentFlowListener Listener for payment operation callbacks
         * @return Result containing the state or an error if fetching failed
         */
        suspend fun create(
            session: AirwallexSession,
            airwallex: Airwallex,
            configuration: PaymentElementConfiguration,
            paymentFlowListener: PaymentFlowListener
        ): Result<PaymentElement> {
            return create(
                session,
                airwallex,
                configuration,
                paymentFlowListener,
                AnalyticsLogger.LaunchType.EMBEDDED
            )
        }

        /**
         * Internal create function that allows specifying the launch type.
         * Only used by SDK internal activities (e.g., PaymentMethodsActivity).
         *
         * @param session The Airwallex session containing payment information
         * @param airwallex The Airwallex instance for payment operations
         * @param configuration Configuration for the payment element
         * @param paymentFlowListener Listener for payment operation callbacks
         * @param launchType The launch type for analytics (COMPONENT, DROPIN, EMBEDDED, API)
         * @return Result containing the state or an error if fetching failed
         */
        internal suspend fun create(
            session: AirwallexSession,
            airwallex: Airwallex,
            configuration: PaymentElementConfiguration,
            paymentFlowListener: PaymentFlowListener,
            launchType: String
        ): Result<PaymentElement> {
            if (configuration is PaymentElementConfiguration.Card && configuration.supportedCardBrands.isEmpty()) {
                return Result.failure(InvalidParamsException("supportedCardBrands should not be empty"))
            }

            // Bind session's PaymentIntentProvider to this Activity's lifecycle
            session.bindToActivity(airwallex.activity)

            val viewModel = ViewModelProvider(
                airwallex.activity,
                PaymentFlowViewModel.Factory(
                    airwallex = airwallex,
                    session = session
                )
            )[PaymentFlowViewModel::class.java]
            viewModel.updateActivity(airwallex.activity)

            observeState(airwallex, viewModel, paymentFlowListener)

            val alreadyLoaded = viewModel.availablePaymentMethods.value.isNotEmpty()

            val layout = when (configuration) {
                is PaymentElementConfiguration.PaymentSheet -> configuration.layout.toAnalyticsLayoutString()
                else -> null
            }
            val showsGooglePayAsPrimaryButton = when (configuration) {
                is PaymentElementConfiguration.PaymentSheet -> configuration.showsGooglePayAsPrimaryButton
                else -> null
            }
            AnalyticsLogger.setupSession(session, launchType, layout, showsGooglePayAsPrimaryButton)
            val additionalInfo = mutableMapOf<String, String>()
            if (configuration is PaymentElementConfiguration.Card) {
                additionalInfo[Field.PAYMENT_METHOD] = PaymentMethodType.CARD.value
            }
            AnalyticsLogger.logAction(
                actionName = AnalyticsConstants.EVENT_PAYMENT_LAUNCHED,
                additionalInfo = additionalInfo
            )
            val shouldFetch = configuration is PaymentElementConfiguration.PaymentSheet && !alreadyLoaded

            return if (shouldFetch) {
                viewModel.fetchAvailablePaymentMethodsAndConsents()
                    .map { (_, _) ->
                        PaymentElement(
                            session = session,
                            airwallex = airwallex,
                            configuration = configuration,
                            flowViewModel = viewModel,
                            paymentFlowListener = paymentFlowListener
                        )
                    }
            } else {
                // Data already loaded or no fetching needed
                Result.success(
                    PaymentElement(
                        session = session,
                        airwallex = airwallex,
                        configuration = configuration,
                        flowViewModel = viewModel,
                        paymentFlowListener = paymentFlowListener
                    )
                )
            }
        }

        @Suppress("LongParameterList")
        suspend fun create(
            session: AirwallexSession,
            airwallex: Airwallex,
            configuration: PaymentElementConfiguration,
            onLoadingStateChanged: ((Boolean) -> Unit)? = null,
            onPaymentResult: (AirwallexPaymentStatus) -> Unit,
            onError: ((Throwable) -> Unit)? = null
        ): Result<PaymentElement> {
            return create(
                session,
                airwallex,
                configuration,
                onLoadingStateChanged,
                onPaymentResult,
                onError,
                AnalyticsLogger.LaunchType.EMBEDDED
            )
        }

        /**
         * Internal create function with callback style that allows specifying the launch type.
         * Only used by SDK internal activities.
         */
        @Suppress("LongParameterList")
        internal suspend fun create(
            session: AirwallexSession,
            airwallex: Airwallex,
            configuration: PaymentElementConfiguration,
            onLoadingStateChanged: ((Boolean) -> Unit)? = null,
            onPaymentResult: (AirwallexPaymentStatus) -> Unit,
            onError: ((Throwable) -> Unit)? = null,
            launchType: String
        ): Result<PaymentElement> {
            val listener = object : PaymentFlowListener {
                override fun onLoadingStateChanged(isLoading: Boolean, context: Context) {
                    onLoadingStateChanged?.invoke(isLoading) ?: super.onLoadingStateChanged(isLoading, context)
                }

                override fun onPaymentResult(status: AirwallexPaymentStatus) {
                    onPaymentResult(status)
                }

                override fun onError(exception: Throwable, context: Context) {
                    onError?.invoke(exception) ?: super.onError(exception, context)
                }
            }
            return create(session, airwallex, configuration, listener, launchType)
        }

        /**
         * Java-friendly: Creates a PaymentElement without automatic rendering.
         *
         * Use this from Java code when you want to control rendering yourself.
         * Call [PaymentElement.renderIn] on the element to render it when ready.
         *
         * Example usage from Java:
         * ```java
         * PaymentElement.create(
         *     this,  // ComponentActivity
         *     session,
         *     airwallex,
         *     configuration,
         *     listener,
         *     new PaymentElementCallback() {
         *         @Override
         *         public void onSuccess(PaymentElement element) {
         *             // Render when ready
         *             element.renderIn(composeView);
         *         }
         *
         *         @Override
         *         public void onFailure(Throwable error) {
         *             // Handle error
         *         }
         *     }
         * );
         * ```
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
        fun create(
            session: AirwallexSession,
            airwallex: Airwallex,
            configuration: PaymentElementConfiguration,
            paymentFlowListener: PaymentFlowListener,
            callback: PaymentElementCallback
        ) {
            airwallex.activity.lifecycleScope.launch {
                val result = create(
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

    }

    /**
     * Renders this PaymentElement in the given ComposeView.
     *
     * Call this method after successfully creating a PaymentElement to display it in your UI.
     * This is typically called in the `onSuccess` callback of [create].
     *
     * Example (Java):
     * ```java
     * PaymentElement.create(..., new PaymentElementCallback() {
     *     @Override
     *     public void onSuccess(PaymentElement element) {
     *         element.renderIn(composeView);
     *     }
     * });
     * ```
     *
     * @param composeView The ComposeView where the payment UI will be rendered
     */
    fun renderIn(composeView: ComposeView) {
        composeView.setContent {
            Content()
        }
    }

    /**
     * Renders the payment element composable using pre-fetched data from the ViewModel.
     *
     * This composable does NOT fetch data - all data is already loaded during [create].
     * It always uses the [PaymentFlowListener] provided at creation.
     *
     * Note: For Java developers, use [renderIn] instead of calling this directly.
     */
    @Composable
    fun Content() {
        AirwallexTheme {
            PaymentElementComponent(
                session = session,
                airwallex = airwallex,
                configuration = configuration,
                paymentFlowListener = paymentFlowListener,
                flowViewModel = flowViewModel
            )
        }
    }
}

private fun observeState(
    airwallex: Airwallex,
    viewModel: PaymentFlowViewModel,
    paymentFlowListener: PaymentFlowListener
) {
    airwallex.activity.lifecycleScope.launch {
        airwallex.activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.paymentResult.collect { result ->
                paymentFlowListener.onLoadingStateChanged(false, airwallex.activity)
                paymentFlowListener.onPaymentResult(result.status)
            }
        }
    }
}

/**
 * Callback interface for PaymentElement creation results.
 *
 * Implement this interface to receive callbacks when PaymentElement creation
 * succeeds or fails. This is primarily for Java interoperability.
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