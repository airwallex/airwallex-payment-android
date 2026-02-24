package com.airwallex.android.view.composables

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.toAnalyticsLayoutString
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.view.PaymentFlowListener
import com.airwallex.android.view.PaymentFlowViewModel
import com.airwallex.android.view.composables.PaymentElement.Companion.create

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
            val viewModel = ViewModelProvider(
                airwallex.activity,
                PaymentFlowViewModel.Factory(
                    airwallex = airwallex,
                    session = session
                )
            )[PaymentFlowViewModel::class.java]
            viewModel.updateActivity(airwallex.activity)
            val alreadyLoaded = viewModel.availablePaymentMethods.value.isNotEmpty()

            val layout = when (configuration) {
                is PaymentElementConfiguration.PaymentSheet -> configuration.layout.toAnalyticsLayoutString()
                else -> null
            }
            AnalyticsLogger.setupSession(session, launchType, layout)

            // Determine if we need to fetch based on configuration
            val shouldFetch = when (configuration) {
                is PaymentElementConfiguration.Card -> configuration.supportedCardBrands.isEmpty()
                is PaymentElementConfiguration.PaymentSheet -> true
            } && !alreadyLoaded

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
            onLoadingStateChanged: ((Boolean) -> Unit)?,
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
    }

    /**
     * Renders the payment element composable using pre-fetched data from the ViewModel.
     *
     * This composable does NOT fetch data - all data is already loaded during [create].
     * It always uses the [PaymentFlowListener] provided at creation.
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