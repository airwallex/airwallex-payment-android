package com.airwallex.android.view.composables

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.ui.R
import com.airwallex.android.view.PaymentOperationListener
import com.airwallex.android.view.PaymentOperationsViewModel

class AwxPaymentElementManager private constructor(
    private val session: AirwallexSession,
    private val airwallex: Airwallex,
    private val configuration: AwxPaymentElementConfiguration,
    private val operationsViewModel: PaymentOperationsViewModel,
    private val operationListener: PaymentOperationListener
) {

    companion object {
        /**
         * Creates an [AwxPaymentElementManager] by fetching required data.
         *
         * This method obtains a [PaymentOperationsViewModel] scoped to the Activity,
         * checks if data is already loaded (for configuration changes), and fetches
         * if necessary.
         *
         * @param session The Airwallex session containing payment information
         * @param airwallex The Airwallex instance for payment operations
         * @param configuration Configuration for the payment element
         * @param operationListener Listener for payment operation callbacks
         * @return Result containing the state or an error if fetching failed
         */
        suspend fun create(
            session: AirwallexSession,
            airwallex: Airwallex,
            configuration: AwxPaymentElementConfiguration,
            operationListener: PaymentOperationListener
        ): Result<AwxPaymentElementManager> {
            val viewModel = ViewModelProvider(
                airwallex.activity,
                PaymentOperationsViewModel.Factory(
                    airwallex = airwallex,
                    session = session
                )
            )[PaymentOperationsViewModel::class.java]
            viewModel.updateActivity(airwallex.activity)
            val alreadyLoaded = viewModel.availablePaymentMethods.value.isNotEmpty()

            // Determine if we need to fetch based on configuration
            val shouldFetch = when (configuration) {
                is AwxPaymentElementConfiguration.Card -> configuration.cardSchemes.isEmpty()
                is AwxPaymentElementConfiguration.PaymentSheet -> true
            } && !alreadyLoaded

            return if (shouldFetch) {
                viewModel.fetchAvailablePaymentMethodsAndConsents()
                    .map { (_, _) ->
                        AwxPaymentElementManager(
                            session = session,
                            airwallex = airwallex,
                            configuration = configuration,
                            operationsViewModel = viewModel,
                            operationListener = operationListener
                        )
                    }
            } else {
                // Data already loaded or no fetching needed
                Result.success(
                    AwxPaymentElementManager(
                        session = session,
                        airwallex = airwallex,
                        configuration = configuration,
                        operationsViewModel = viewModel,
                        operationListener = operationListener
                    )
                )
            }
        }

        suspend fun create(
            session: AirwallexSession,
            airwallex: Airwallex,
            configuration: AwxPaymentElementConfiguration,
            onLoadingStateChanged: (Boolean) -> Unit,
            onPaymentResult: (AirwallexPaymentStatus) -> Unit,
            onError: ((Throwable) -> Unit)? = null
        ): Result<AwxPaymentElementManager> {
            val listener = object : PaymentOperationListener {
                override fun onLoadingStateChanged(isLoading: Boolean) {
                    onLoadingStateChanged(isLoading)
                }

                override fun onPaymentResult(status: AirwallexPaymentStatus) {
                    onPaymentResult(status)
                }

                override fun onError(exception: Throwable) {
                    onError ?: alert(
                        message = exception.message ?: exception.toString(),
                        activity = airwallex.activity
                    )
                }
            }
            return create(session, airwallex, configuration, listener)
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

    /**
     * Renders the payment element composable using pre-fetched data from the ViewModel.
     *
     * This composable does NOT fetch data - all data is already loaded during [create].
     * It always uses the [PaymentOperationListener] provided at creation.
     */
    @Composable
    fun Content() {
        AwxPaymentElement(
            session = session,
            airwallex = airwallex,
            configuration = configuration,
            operationListener = operationListener,
            operationsViewModel = operationsViewModel
        )
    }
}
