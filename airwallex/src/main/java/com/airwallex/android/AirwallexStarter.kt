package com.airwallex.android

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexConfiguration
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.AirwallexShippingStatus
import com.airwallex.android.core.ClientSecretProvider
import com.airwallex.android.core.PaymentResultManager
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.ui.AirwallexActivityLaunch
import com.airwallex.android.view.PaymentMethodsActivityLaunch
import com.airwallex.android.view.PaymentShippingActivityLaunch

/**
 *  Entry-point to the Airwallex Payment Flow. Create a AirwallexStarter attached to the given host Activity.
 */
class AirwallexStarter {

    companion object {
        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(
            application: Application,
            configuration: AirwallexConfiguration,
            clientSecretProvider: ClientSecretProvider? = null
        ) {
            AirwallexActivityLaunch.initialize(application)
            Airwallex.initialize(application, configuration, clientSecretProvider)
        }

        /**
         * Launch the shipping flow to allow the user to fill the shipping information
         *
         * @param fragment fragment {@link Fragment}
         * @param shipping a [Shipping] used to present the shipping flow, it's optional
         * @param shippingResultListener The callback of present the shipping flow
         */
        fun presentShippingFlow(
            fragment: Fragment,
            shipping: Shipping?,
            shippingResultListener: Airwallex.ShippingResultListener
        ) {
            presentShippingFlow(
                PaymentShippingActivityLaunch(fragment),
                shipping,
                shippingResultListener
            )
        }

        /**
         * Launch the shipping flow to allow the user to fill the shipping information
         *
         * @param activity activity {@link Activity}
         * @param shipping a [Shipping] used to present the shipping flow, it's optional
         * @param shippingResultListener The callback of present the shipping flow
         */
        fun presentShippingFlow(
            activity: Activity,
            shipping: Shipping?,
            shippingResultListener: Airwallex.ShippingResultListener
        ) {
            presentShippingFlow(
                PaymentShippingActivityLaunch(activity),
                shipping,
                shippingResultListener
            )
        }

        private fun presentShippingFlow(
            launch: PaymentShippingActivityLaunch,
            shipping: Shipping?,
            shippingResultListener: Airwallex.ShippingResultListener
        ) {
            launch.launchForResult(
                PaymentShippingActivityLaunch.Args.Builder()
                    .setShipping(shipping)
                    .build()
            ) { _, result ->
                handleShippingPaymentData(result.resultCode, result.data, shippingResultListener)
            }
        }

        /**
         * Launch the payment flow to allow the user to complete the entire payment flow
         *
         * @param fragment fragment {@link Fragment}
         * @param session a [AirwallexSession] used to present the payment flow
         * @param paymentResultListener The callback of present entire payment flow
         */
        fun presentPaymentFlow(
            fragment: Fragment,
            session: AirwallexSession,
            paymentResultListener: Airwallex.PaymentResultListener
        ) {
            presentPaymentFlow(
                PaymentMethodsActivityLaunch(fragment),
                session,
                paymentResultListener
            )
        }

        /**
         * Launch the payment flow to allow the user to complete the entire payment flow
         *
         * @param activity activity {@link Activity}
         * @param session a [AirwallexSession] used to present the payment flow
         * @param paymentResultListener The callback of present entire payment flow
         */
        fun presentPaymentFlow(
            activity: Activity,
            session: AirwallexSession,
            paymentResultListener: Airwallex.PaymentResultListener
        ) {
            presentPaymentFlow(
                PaymentMethodsActivityLaunch(activity),
                session,
                paymentResultListener
            )
        }

        private fun presentPaymentFlow(
            launch: PaymentMethodsActivityLaunch,
            session: AirwallexSession,
            paymentResultListener: Airwallex.PaymentResultListener
        ) {
            PaymentResultManager.getInstance(paymentResultListener)
            launch.launchForResult(
                PaymentMethodsActivityLaunch.Args.Builder()
                    .setAirwallexSession(session)
                    .build()
            ) { _, result ->
                handlePaymentData(result.resultCode, result.data, paymentResultListener)
            }
        }

        /**
         * Method to handle Activity results from Airwallex activities.
         *
         * @param resultCode a result code representing the success of the intended action
         * @param data an [Intent] with the resulting data from the Activity
         *
         */
        private fun handleShippingPaymentData(
            resultCode: Int,
            data: Intent?,
            shippingResultListener: Airwallex.ShippingResultListener
        ) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val result = PaymentShippingActivityLaunch.Result.fromIntent(data)
                    if (result == null) {
                        shippingResultListener.onCompleted(
                            AirwallexShippingStatus.Failure(AirwallexCheckoutException(message = "shipping result is null"))
                        )
                        return
                    }
                    shippingResultListener.onCompleted(
                        AirwallexShippingStatus.Success(result.shipping)
                    )
                }

                Activity.RESULT_CANCELED -> {
                    shippingResultListener.onCompleted(AirwallexShippingStatus.Cancel)
                }

            }
        }

        /**
         * Method to handle Activity results from Airwallex activities.
         *
         * @param resultCode a result code representing the success of the intended action
         * @param data an [Intent] with the resulting data from the Activity
         *
         */
        private fun handlePaymentData(
            resultCode: Int,
            data: Intent?,
            paymentResultListener: Airwallex.PaymentResultListener
        ) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val result = PaymentMethodsActivityLaunch.Result.fromIntent(data)
                    if (result == null) {
                        paymentResultListener.onCompleted(
                            AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "flow result is null"))
                        )
                        return
                    }
                    when {
                        result.exception != null -> {
                            paymentResultListener.onCompleted(
                                AirwallexPaymentStatus.Failure(result.exception)
                            )
                        }

                        result.paymentIntentId != null -> {
                            if (result.isRedirecting) {
                                paymentResultListener.onCompleted(
                                    AirwallexPaymentStatus.InProgress(result.paymentIntentId)
                                )
                            } else {
                                paymentResultListener.onCompleted(
                                    AirwallexPaymentStatus.Success(result.paymentIntentId)
                                )
                            }
                        }
                    }
                }

                Activity.RESULT_CANCELED -> {
                    paymentResultListener.onCompleted(AirwallexPaymentStatus.Cancel)
                }
            }
        }
    }
}
