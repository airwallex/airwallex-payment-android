package com.airwallex.android.view.composables

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.R
import com.airwallex.android.view.PaymentOperationListener
import com.airwallex.android.view.PaymentOperationsViewModel
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.android.view.util.getSinglePaymentMethodOrNull

/**
 * State holder for [AwxPaymentElement] with async initialization pattern.
 *
 * This class pre-fetches payment methods and consents before mounting the composable,
 * allowing for explicit error handling and retry logic. The class owns a [PaymentOperationsViewModel]
 * that is scoped to the Activity, ensuring data survives configuration changes.
 *
 * Usage:
 * ```
 * // In a coroutine scope (e.g., LaunchedEffect, ViewModel)
 * val state = AwxPaymentElementState.create(
 *     session = session,
 *     airwallex = airwallex,
 *     configuration = AwxPaymentElementConfiguration.Card()
 * ).getOrElse { error ->
 *     // Handle initialization error
 *     showError(error.message)
 *     showRetryButton()
 *     return
 * }
 *
 * // In composable - data is already loaded
 * state.Content(
 *     onPaymentResult = { status -> handleResult(status) },
 *     onError = { error -> showError(error) }
 * )
 * ```
 */
class AwxPaymentElementState private constructor(
    private val session: AirwallexSession,
    private val airwallex: Airwallex,
    private val configuration: AwxPaymentElementConfiguration,
    private val operationsViewModel: PaymentOperationsViewModel
) {

    companion object {
        /**
         * Creates an [AwxPaymentElementState] by fetching required data.
         *
         * This method obtains a [PaymentOperationsViewModel] scoped to the Activity,
         * checks if data is already loaded (for configuration changes), and fetches
         * if necessary.
         *
         * @param session The Airwallex session containing payment information
         * @param airwallex The Airwallex instance for payment operations
         * @param configuration Configuration for the payment element
         * @return Result containing the state or an error if fetching failed
         */
        suspend fun create(
            session: AirwallexSession,
            airwallex: Airwallex,
            configuration: AwxPaymentElementConfiguration
        ): Result<AwxPaymentElementState> {
            val viewModel = ViewModelProvider(
                airwallex.activity,
                PaymentOperationsViewModel.Factory(
                    airwallex = airwallex,
                    session = session
                )
            )[PaymentOperationsViewModel::class.java]

            val alreadyLoaded = viewModel.availablePaymentMethods.value.isNotEmpty()

            // Determine if we need to fetch based on configuration
            val shouldFetch = when (configuration) {
                is AwxPaymentElementConfiguration.Card -> configuration.cardSchemes.isEmpty()
                is AwxPaymentElementConfiguration.PaymentSheet -> true
            } && !alreadyLoaded

            return if (shouldFetch) {
                viewModel.fetchAvailablePaymentMethodsAndConsents()
                    .map { (_, _) ->
                        AwxPaymentElementState(
                            session = session,
                            airwallex = airwallex,
                            configuration = configuration,
                            operationsViewModel = viewModel
                        )
                    }
            } else {
                // Data already loaded or no fetching needed
                Result.success(
                    AwxPaymentElementState(
                        session = session,
                        airwallex = airwallex,
                        configuration = configuration,
                        operationsViewModel = viewModel
                    )
                )
            }
        }
    }

    /**
     * Renders the payment element composable using pre-fetched data from the ViewModel.
     *
     * This composable does NOT fetch data - all data is already loaded during [create].
     * It delegates to [AwxPaymentElement] with the lambda callback overload.
     *
     * @param onLoadingStateChanged Callback invoked when loading state changes (required)
     * @param onPaymentResult Callback invoked when a payment operation completes (required)
     * @param onError Callback invoked when an error occurs. Null uses default (shows AlertDialog).
     */
    @Composable
    fun Content(
        onLoadingStateChanged: (Boolean) -> Unit,
        onPaymentResult: (AirwallexPaymentStatus) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ) {
        AwxPaymentElement(
            session = session,
            airwallex = airwallex,
            configuration = configuration,
            onLoadingStateChanged = onLoadingStateChanged,
            onPaymentResult = onPaymentResult,
            onError = onError ?: { exception ->
                alert(
                    message = exception.message ?: exception.toString(),
                    activity = airwallex.activity
                )
            },
            operationsViewModel = operationsViewModel
        )
    }

    /**
     * Renders the payment element composable using pre-fetched data from the ViewModel.
     *
     * This overload accepts a [PaymentOperationListener] - either provide all callbacks
     * via the listener, or omit it to use default implementations.
     *
     * @param operationListener Listener for all payment operation callbacks. Null uses defaults.
     */
    @Composable
    fun Content(
        operationListener: com.airwallex.android.view.PaymentOperationListener? = null
    ) {
        val listener = operationListener ?: createDefaultListener()

        AwxPaymentElement(
            session = session,
            airwallex = airwallex,
            configuration = configuration,
            operationListener = listener,
            operationsViewModel = operationsViewModel
        )
    }

    private fun createDefaultListener(): com.airwallex.android.view.PaymentOperationListener {
        return object : PaymentOperationListener {
            override fun onLoadingStateChanged(isLoading: Boolean) {
                // Default: no-op
            }

            override fun onPaymentResult(status: AirwallexPaymentStatus) {
                // Default: no-op (user should handle result)
            }

            override fun onError(exception: Throwable) {
                // Default: show error dialog
                alert(
                    message = exception.message ?: exception.toString(),
                    activity = airwallex.activity
                )
            }
        }
    }

    private fun alert(title: String = "", message: String, activity: ComponentActivity) {
        if (!activity.isFinishing) {
            AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.airwallex_okay) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }
    }
}
