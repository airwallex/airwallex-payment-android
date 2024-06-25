package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.AirwallexShippingStatus
import com.airwallex.android.core.PaymentResultManager
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.ui.AirwallexActivityLaunch
import com.airwallex.android.view.PaymentMethodsActivityLaunch
import com.airwallex.android.view.PaymentShippingActivityLaunch

/**
 *  Entry-point to the Airwallex Payment Flow. Create a AirwallexStarter attached to the given host Activity.
 */
class AirwallexStarter {

    companion object {
        private var shippingResultListener: Airwallex.ShippingResultListener? = null
        private var paymentResultListener: Airwallex.PaymentResultListener? = null

        private val VALID_REQUEST_CODES = setOf(
            PaymentMethodsActivityLaunch.REQUEST_CODE,
            PaymentShippingActivityLaunch.REQUEST_CODE
        )

        fun registerLifecycle(lifecycleOwner: LifecycleOwner) {
            AirwallexActivityLaunch.registerForActivityResult(lifecycleOwner) { requestCode, result ->
                handlePaymentData(requestCode, resultCode = result.resultCode, result.data)
            }
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
            this.shippingResultListener = shippingResultListener
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
            this.paymentResultListener = paymentResultListener
            PaymentResultManager.getInstance(paymentResultListener)
            launch.launchForResult(
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
                            shippingResultListener?.onCompleted(
                                AirwallexShippingStatus.Success(result.shipping)
                            )
                            shippingResultListener = null
                            true
                        }
                        PaymentMethodsActivityLaunch.REQUEST_CODE -> {
                            val result =
                                PaymentMethodsActivityLaunch.Result.fromIntent(data) ?: return true
                            when {
                                result.exception != null -> {
                                    paymentResultListener?.onCompleted(
                                        AirwallexPaymentStatus.Failure(result.exception)
                                    )
                                }
                                result.paymentIntentId != null -> {
                                    if (result.isRedirecting) {
                                        paymentResultListener?.onCompleted(
                                            AirwallexPaymentStatus.InProgress(result.paymentIntentId)
                                        )
                                    } else {
                                        paymentResultListener?.onCompleted(
                                            AirwallexPaymentStatus.Success(result.paymentIntentId)
                                        )
                                    }
                                }
                            }
                            paymentResultListener = null
                            true
                        }
                        else -> false
                    }
                }
                Activity.RESULT_CANCELED -> {
                    return when (requestCode) {
                        PaymentShippingActivityLaunch.REQUEST_CODE -> {
                            shippingResultListener?.onCompleted(AirwallexShippingStatus.Cancel)
                            shippingResultListener = null
                            true
                        }
                        PaymentMethodsActivityLaunch.REQUEST_CODE -> {
                            paymentResultListener?.onCompleted(AirwallexPaymentStatus.Cancel)
                            paymentResultListener = null
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
