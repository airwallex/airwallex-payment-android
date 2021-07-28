package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.model.*
import com.airwallex.android.view.*

/**
 *  Entry-point to the Airwallex Payment Flow. Create a AirwallexStarter attached to the given host Activity.
 *
 *  @param activity This Activity will receive results in `Activity#onActivityResult(int, int, Intent)` that should be passed back to this session.
 *  @param paymentShippingActivityLaunch instance of [PaymentShippingActivityLaunch]
 *  @param paymentMethodsActivityLaunch instance of [PaymentMethodsActivityLaunch]
 */
internal class AirwallexStarter constructor(
    private val activity: Activity,
    private val paymentShippingActivityLaunch: PaymentShippingActivityLaunch,
    private val paymentMethodsActivityLaunch: PaymentMethodsActivityLaunch
) {

    constructor(
        fragment: Fragment
    ) : this(
        fragment.requireActivity(),
        PaymentShippingActivityLaunch(fragment),
        PaymentMethodsActivityLaunch(fragment)
    )

    constructor(
        activity: Activity
    ) : this(
        activity,
        PaymentShippingActivityLaunch(activity),
        PaymentMethodsActivityLaunch(activity)
    )

    private var shippingFlowListener: Airwallex.PaymentShippingListener? = null
    private var paymentFlowListener: Airwallex.PaymentIntentListener? = null

    /**
     * Launch the [PaymentShippingActivity] to allow the user to fill the shipping information
     *
     * @param shipping a [Shipping] used to present the shipping flow, it's optional
     * @param shippingFlowListener The callback of present the shipping flow
     */
    fun presentShippingFlow(
        shipping: Shipping? = null,
        shippingFlowListener: Airwallex.PaymentShippingListener
    ) {
        this.shippingFlowListener = shippingFlowListener
        paymentShippingActivityLaunch.startForResult(
            PaymentShippingActivityLaunch.Args.Builder()
                .setShipping(shipping)
                .build()
        )
    }

    /**
     * Launch the [PaymentMethodsActivity] to allow the user to complete the entire payment flow
     *
     * @param session a [AirwallexSession] used to present the payment flow
     * @param clientSecretProvider a [ClientSecretProvider] used to generate client-secret, not required for anonymous payments
     * @param paymentFlowListener The callback of present entire payment flow
     */
    fun presentPaymentFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider? = null,
        paymentFlowListener: Airwallex.PaymentIntentListener
    ) {
        this.paymentFlowListener = paymentFlowListener
        clientSecretProvider?.let {
            ClientSecretRepository.init(it)
        }
        paymentMethodsActivityLaunch.startForResult(
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
    fun onActivityResult(
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
                            result.weChat != null -> {
                                paymentFlowListener?.onNextActionWithWeChatPay(result.weChat)
                            }
                            result.redirectUrl != null -> {
                                paymentFlowListener?.onNextActionWithRedirectUrl(result.redirectUrl)
                            }
                            result.paymentIntent != null -> {
                                paymentFlowListener?.onSuccess(result.paymentIntent)
                            }
                            else -> {
                                paymentFlowListener?.onFailed(Exception("Unknown exception."))
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

    internal companion object {

        private val VALID_REQUEST_CODES = setOf(
            PaymentMethodsActivityLaunch.REQUEST_CODE,
            PaymentShippingActivityLaunch.REQUEST_CODE
        )
    }
}
