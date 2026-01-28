package com.airwallex.paymentacceptance.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.ui.composables.AirwallexTheme
import com.airwallex.android.view.AddPaymentMethodViewModel
import com.airwallex.android.view.PaymentMethodsViewModel
import com.airwallex.android.view.composables.PaymentMethodsTabSection
import com.airwallex.android.view.composables.card.CardSection
import com.airwallex.paymentacceptance.viewmodel.EmbeddedPaymentViewModel

class EmbeddedPaymentActivity : ComponentActivity() {

    private val session: AirwallexSession by lazy {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_SESSION, AirwallexSession::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_SESSION)
        } ?: throw IllegalArgumentException("Session is required")
    }

    // TODO: Dummy for now. ideally client shouldn't know about this, card schemes will be generated in the sdk
    private val cardSchemes: List<CardScheme> by lazy {
        listOf(
            CardScheme("visa"),
            CardScheme("mastercard"),
            CardScheme("amex")
        )
    }

    private val airwallex: Airwallex by lazy {
        Airwallex(this)
    }

    private val viewModel: EmbeddedPaymentViewModel by lazy {
        ViewModelProvider(
            this,
            EmbeddedPaymentViewModel.Factory(application, airwallex, session)
        )[EmbeddedPaymentViewModel::class.java]
    }

    private val paymentMethodsViewModel: PaymentMethodsViewModel by lazy {
        ViewModelProvider(
            this,
            PaymentMethodsViewModel.Factory(application, airwallex, session)
        )[PaymentMethodsViewModel::class.java]
    }

    private val addPaymentMethodViewModel: AddPaymentMethodViewModel by lazy {
        ViewModelProvider(
            this,
            AddPaymentMethodViewModel.Factory(application, airwallex, session, cardSchemes)
        )[AddPaymentMethodViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AirwallexTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }

    @Composable
    fun MainContent() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Below is the embedded section for list of available payments",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodsTabSection(
                session = session,
                airwallex = airwallex,
                paymentMethodViewModel = paymentMethodsViewModel,
                addPaymentMethodViewModel = addPaymentMethodViewModel,
                onDeleteCard = { consent ->
                    viewModel.deleteCard(consent) {
                        addPaymentMethodViewModel.deleteCardSuccess(consent)
                    }
                },
                onCheckoutWithoutCvc = { consent ->
                    viewModel.checkoutWithoutCvc(consent)
                },
                onCheckoutWithCvc = { consent, cvc ->
                    viewModel.checkoutWithCvc(consent, cvc)
                },
                onDirectPay = { type ->
                    viewModel.directPay(type)
                },
                onPayWithFields = { method, typeInfo, fields ->
                    viewModel.payWithFields(method, typeInfo, fields)
                },
                onLoading = { isLoading ->
                    // Handle loading state if needed
                },
                onCardLoadingChanged = { operation ->
                    // Handle card loading state if needed
                },
                onCardPaymentResult = { status ->
                    handlePaymentResult(status)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "And below is the embedded section for card only",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            CardSection(
                session = session,
                airwallex = airwallex,
                addPaymentMethodViewModel = addPaymentMethodViewModel,
                cardSchemes = cardSchemes,
                onDeleteCard = { consent ->
                    viewModel.deleteCard(consent) {
                        addPaymentMethodViewModel.deleteCardSuccess(consent)
                    }
                },
                onCheckoutWithoutCvc = { consent ->
                    viewModel.checkoutWithoutCvc(consent)
                },
                onCheckoutWithCvc = { consent, cvc ->
                    viewModel.checkoutWithCvc(consent, cvc)
                },
                isSinglePaymentMethod = false,
                onLoadingChanged = { operation ->
                    // Handle loading state if needed
                },
                onPaymentResult = { status ->
                    handlePaymentResult(status)
                }
            )
        }
    }

    private fun handlePaymentResult(status: AirwallexPaymentStatus) {
        when (status) {
            is AirwallexPaymentStatus.Success -> {
                showAlert(
                    "Payment Success!",
                    "Payment Intent ID: ${status.paymentIntentId}"
                )
            }
            is AirwallexPaymentStatus.Failure -> {
                showAlert(
                    "Payment Failed",
                    status.exception.message ?: "An error occurred during payment"
                )
            }
            is AirwallexPaymentStatus.Cancel -> {
                showAlert(
                    "Payment Cancelled",
                    "The payment was cancelled by the user"
                )
            }
            is AirwallexPaymentStatus.InProgress -> {
                showAlert(
                    "Payment In Progress",
                    "Payment is being processed. Please wait..."
                )
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }

    companion object {
        private const val EXTRA_SESSION = "extra_session"

        fun start(activity: Activity, session: AirwallexSession) {
            val intent = Intent(activity, EmbeddedPaymentActivity::class.java).apply {
                putExtra(EXTRA_SESSION, session)
            }
            activity.startActivity(intent)
        }
    }
}
