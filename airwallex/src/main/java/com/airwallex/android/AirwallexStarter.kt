package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.*
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.view.PaymentMethodsActivityLaunch
import com.airwallex.android.view.PaymentShippingActivityLaunch

/**
 *  Entry-point to the Airwallex Payment Flow. Create a AirwallexStarter attached to the given host Activity.
 */
class AirwallexStarter {

    interface PaymentFlowListener<T> {
        fun onSuccess(response: T)
        fun onFailed(exception: AirwallexException)
        fun onCancelled()
    }

    companion object {
        private var shippingFlowListener: PaymentFlowListener<Shipping>? = null
        private var paymentFlowListener: PaymentFlowListener<String>? = null

        private val VALID_REQUEST_CODES = setOf(
            PaymentMethodsActivityLaunch.REQUEST_CODE,
            PaymentShippingActivityLaunch.REQUEST_CODE
        )

        /**
         * Launch the shipping flow to allow the user to fill the shipping information
         *
         * @param fragment fragment {@link Fragment}
         * @param shipping a [Shipping] used to present the shipping flow, it's optional
         * @param shippingFlowListener The callback of present the shipping flow
         */
        fun presentShippingFlow(
            fragment: Fragment,
            shipping: Shipping?,
            shippingFlowListener: PaymentFlowListener<Shipping>
        ) {
            presentShippingFlow(
                PaymentShippingActivityLaunch(fragment),
                shipping,
                shippingFlowListener
            )
        }

        /**
         * Launch the shipping flow to allow the user to fill the shipping information
         *
         * @param activity activity {@link Activity}
         * @param shipping a [Shipping] used to present the shipping flow, it's optional
         * @param shippingFlowListener The callback of present the shipping flow
         */
        fun presentShippingFlow(
            activity: Activity,
            shipping: Shipping?,
            shippingFlowListener: PaymentFlowListener<Shipping>
        ) {
            presentShippingFlow(
                PaymentShippingActivityLaunch(activity),
                shipping,
                shippingFlowListener
            )
        }

        private fun presentShippingFlow(
            launch: PaymentShippingActivityLaunch,
            shipping: Shipping?,
            shippingFlowListener: PaymentFlowListener<Shipping>
        ) {
            this.shippingFlowListener = shippingFlowListener
            launch.startForResult(
                PaymentShippingActivityLaunch.Args.Builder()
                    .setShipping(shipping)
                    .build()
            )
        }

        /**
         * Launch the payment flow to allow the user to complete the entire payment flow
         *
         * @param fragment fragment {@link Fragment}
         * @param session a [AirwallexSession] used to present the payment flow
         * @param clientSecretProvider a [ClientSecretProvider] used to generate client-secret, not required for anonymous payments
         * @param paymentFlowListener The callback of present entire payment flow
         */
        fun presentPaymentFlow(
            fragment: Fragment,
            session: AirwallexSession,
            clientSecretProvider: ClientSecretProvider?,
            paymentFlowListener: PaymentFlowListener<String>
        ) {
            presentPaymentFlow(
                PaymentMethodsActivityLaunch(fragment),
                session,
                clientSecretProvider,
                paymentFlowListener
            )
        }

        /**
         * Launch the payment flow to allow the user to complete the entire payment flow
         *
         * @param activity activity {@link Activity}
         * @param session a [AirwallexSession] used to present the payment flow
         * @param clientSecretProvider a [ClientSecretProvider] used to generate client-secret, not required for anonymous payments
         * @param paymentFlowListener The callback of present entire payment flow
         */
        fun presentPaymentFlow(
            activity: Activity,
            session: AirwallexSession,
            clientSecretProvider: ClientSecretProvider?,
            paymentFlowListener: PaymentFlowListener<String>
        ) {
            presentPaymentFlow(
                PaymentMethodsActivityLaunch(activity),
                session,
                clientSecretProvider,
                paymentFlowListener
            )
        }

        private fun presentPaymentFlow(
            launch: PaymentMethodsActivityLaunch,
            session: AirwallexSession,
            clientSecretProvider: ClientSecretProvider?,
            paymentFlowListener: PaymentFlowListener<String>
        ) {
            this.paymentFlowListener = paymentFlowListener
            clientSecretProvider?.let {
                ClientSecretRepository.init(it)
            }
            launch.startForResult(
                PaymentMethodsActivityLaunch.Args.Builder()
                    .setAirwallexSession(session)
                    .build()
            )
        }

        /**
         * Method to handle Activity results from Airwallex activities. Pass data here from your
         * host's `#onActivityResult(int, int, Intent)` function.
         *
         * @param requestCode the request code used to open the resulting activity
         * @param resultCode a result code representing the success of the intended action
         * @param data an [Intent] with the resulting data from the Activity
         *
         * @return `true` if the activity result was handled by this function,
         * otherwise `false`
         */
        fun handlePaymentData(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ): Boolean {
            if (!VALID_REQUEST_CODES.contains(requestCode)) {
                return false
            }

            when (resultCode) {
                Activity.RESULT_OK -> {
                    return when (requestCode) {
                        PaymentShippingActivityLaunch.REQUEST_CODE -> {
                            val result =
                                PaymentShippingActivityLaunch.Result.fromIntent(data) ?: return true
                            shippingFlowListener?.onSuccess(result.shipping)
                            shippingFlowListener = null
                            true
                        }
                        PaymentMethodsActivityLaunch.REQUEST_CODE -> {
                            val result =
                                PaymentMethodsActivityLaunch.Result.fromIntent(data) ?: return true
                            when {
                                result.exception != null -> {
                                    paymentFlowListener?.onFailed(result.exception)
                                }
                                result.paymentIntentId != null -> {
                                    paymentFlowListener?.onSuccess(result.paymentIntentId)
                                }
                            }
                            paymentFlowListener = null
                            true
                        }
                        else -> false
                    }
                }
                Activity.RESULT_CANCELED -> {
                    return when (requestCode) {
                        PaymentShippingActivityLaunch.REQUEST_CODE -> {
                            shippingFlowListener?.onCancelled()
                            shippingFlowListener = null
                            true
                        }
                        PaymentMethodsActivityLaunch.REQUEST_CODE -> {
                            paymentFlowListener?.onCancelled()
                            paymentFlowListener = null
                            true
                        }
                        else -> false
                    }
                }
                else -> return false
            }
        }
    }
}
