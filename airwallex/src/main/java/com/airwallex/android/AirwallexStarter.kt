package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.model.*
import com.airwallex.android.view.*

/**
 *  Entry-point to the Airwallex Payment Flow. Create a AirwallexStarter attached to the given host Activity.
 *
 *  @param activity This Activity will receive results in `Activity#onActivityResult(int, int, Intent)`
 *  that should be passed back to this session.
 */
internal class AirwallexStarter constructor(
    private val activity: Activity,
    private val addPaymentMethodActivityLaunch: AddPaymentMethodActivityLaunch,
    private val paymentShippingActivityLaunch: PaymentShippingActivityLaunch,
    private val paymentMethodsActivityLaunch: PaymentMethodsActivityLaunch,
    private val paymentCheckoutActivityLaunch: PaymentCheckoutActivityLaunch
) {

    constructor(
        fragment: Fragment
    ) : this(
        fragment.requireActivity(),
        AddPaymentMethodActivityLaunch(fragment),
        PaymentShippingActivityLaunch(fragment),
        PaymentMethodsActivityLaunch(fragment),
        PaymentCheckoutActivityLaunch(fragment)
    )

    constructor(
        activity: Activity
    ) : this(
        activity,
        AddPaymentMethodActivityLaunch(activity),
        PaymentShippingActivityLaunch(activity),
        PaymentMethodsActivityLaunch(activity),
        PaymentCheckoutActivityLaunch(activity)
    )

    private var shippingFlowListener: Airwallex.PaymentShippingListener? = null
    private var paymentFlowListener: Airwallex.PaymentIntentListener? = null
    private var paymentDetailListener: Airwallex.PaymentIntentCardListener? = null
    private var addPaymentMethodFlowListener: Airwallex.AddPaymentMethodListener? = null
    private var selectPaymentMethodFlowListener: Airwallex.PaymentMethodListener? = null

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
     * Launch the [AddPaymentMethodActivity] to allow the user to add a payment method
     *
     * @param session a [AirwallexSession] used to present the Add Payment Method flow
     * @param clientSecretProvider a [ClientSecretProvider] used to generate client-secret
     * @param addPaymentMethodFlowListener The callback of present the add payment method flow
     */
    fun presentAddPaymentMethodFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider,
        addPaymentMethodFlowListener: Airwallex.AddPaymentMethodListener
    ) {
        if (session.customerId == null) {
            addPaymentMethodFlowListener.onFailed(Exception("Customer id must be provided on AddPaymentMethodFlow"))
            return
        }
        this.addPaymentMethodFlowListener = addPaymentMethodFlowListener
        ClientSecretRepository.init(clientSecretProvider)
        addPaymentMethodActivityLaunch.startForResult(
            AddPaymentMethodActivityLaunch.Args.Builder()
                .setAirwallexSession(session)
                .build()
        )
    }

    /**
     * Launch the [PaymentMethodsActivity] to allow the user to select a payment method or add a new one
     *
     * @param session a [AirwallexSession] used to present the Select Payment Method flow
     * @param clientSecretProvider a [ClientSecretProvider] used to generate client-secret
     * @param selectPaymentMethodFlowListener The callback of present the select payment method flow
     */
    fun presentSelectPaymentMethodFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider,
        selectPaymentMethodFlowListener: Airwallex.PaymentMethodListener
    ) {
        if (session.customerId == null) {
            selectPaymentMethodFlowListener.onFailed(Exception("Customer id must be provided on SelectPaymentMethodFlow"))
            return
        }
        this.selectPaymentMethodFlowListener = selectPaymentMethodFlowListener
        ClientSecretRepository.init(clientSecretProvider)
        paymentMethodsActivityLaunch.startForResult(
            PaymentMethodsActivityLaunch.Args.Builder()
                .setIncludeCheckoutFlow(false)
                .setAirwallexSession(session)
                .build()
        )
    }

    /**
     * Launch the [PaymentCheckoutActivity] to allow the user to confirm [PaymentIntent] using the specified [PaymentMethod]
     *
     * @param session a [AirwallexSession] used to present the Checkout flow
     * @param paymentDetailListener The callback of present the select payment detail flow
     */
    fun presentPaymentDetailFlow(
        session: AirwallexSession,
        paymentDetailListener: Airwallex.PaymentIntentCardListener
    ) {
        val paymentMethod = session.paymentMethod
        if (paymentMethod == null) {
            paymentDetailListener.onFailed(Exception("PaymentMethod is required on PaymentDetailFlow"))
            return
        }
        if (paymentMethod.type != PaymentMethodType.CARD) {
            paymentDetailListener.onFailed(Exception("Only card payment is supported on PaymentDetailFlow"))
            return
        }
        this.paymentDetailListener = paymentDetailListener
        paymentCheckoutActivityLaunch.startForResult(
            PaymentCheckoutActivityLaunch.Args.Builder()
                .setAirwallexSession(session)
                .build()
        )
    }

    /**
     * Launch the [PaymentMethodsActivity] to allow the user to complete the entire payment flow
     *
     * @param session a [AirwallexSession] used to present the payment flow
     * @param clientSecretProvider a [ClientSecretProvider] used to generate client-secret
     * @param paymentFlowListener The callback of present entire payment flow
     */
    fun presentPaymentFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider,
        paymentFlowListener: Airwallex.PaymentIntentListener
    ) {
        if (session.customerId == null) {
            paymentFlowListener.onFailed(Exception("Customer id must be provided on presentPaymentFlow"))
            return
        }
        this.paymentFlowListener = paymentFlowListener
        ClientSecretRepository.init(clientSecretProvider)
        paymentMethodsActivityLaunch.startForResult(
            PaymentMethodsActivityLaunch.Args.Builder()
                .setIncludeCheckoutFlow(true)
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
                    AddPaymentMethodActivityLaunch.REQUEST_CODE -> {
                        val result =
                            AddPaymentMethodActivityLaunch.Result.fromIntent(data) ?: return true
                        addPaymentMethodFlowListener?.onSuccess(result.paymentMethod, result.cvc)
                        addPaymentMethodFlowListener = null
                        true
                    }
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
                        if (result.includeCheckoutFlow) {
                            val exception = result.exception
                            if (exception != null) {
                                paymentFlowListener?.onFailed(exception)
                            } else {
                                when {
                                    result.weChat != null -> {
                                        paymentFlowListener?.onNextActionWithWeChatPay(requireNotNull(result.weChat))
                                    }
                                    result.redirectUrl != null -> {
                                        paymentFlowListener?.onNextActionWithAlipayUrl(requireNotNull(result.redirectUrl))
                                    }
                                    else -> {
                                        paymentFlowListener?.onSuccess(requireNotNull(result.paymentIntent))
                                    }
                                }
                            }
                            paymentFlowListener = null
                        } else {
                            val exception = result.exception
                            if (exception != null) {
                                selectPaymentMethodFlowListener?.onFailed(exception)
                            } else {
                                selectPaymentMethodFlowListener?.onSuccess(
                                    requireNotNull(result.paymentMethod),
                                    result.paymentConsentId,
                                    result.cvc
                                )
                            }
                            selectPaymentMethodFlowListener = null
                        }
                        true
                    }
                    PaymentCheckoutActivityLaunch.REQUEST_CODE -> {
                        val result =
                            PaymentCheckoutActivityLaunch.Result.fromIntent(data) ?: return true
                        val exception = result.exception
                        if (exception != null) {
                            paymentDetailListener?.onFailed(exception)
                        } else {
                            paymentDetailListener?.onSuccess(requireNotNull(result.paymentIntent))
                        }
                        paymentDetailListener = null
                        true
                    }
                    else -> false
                }
            }
            Activity.RESULT_CANCELED -> {
                return when (requestCode) {
                    AddPaymentMethodActivityLaunch.REQUEST_CODE -> {
                        addPaymentMethodFlowListener?.onCancelled()
                        addPaymentMethodFlowListener = null
                        true
                    }
                    PaymentShippingActivityLaunch.REQUEST_CODE -> {
                        shippingFlowListener?.onCancelled()
                        shippingFlowListener = null
                        true
                    }
                    PaymentMethodsActivityLaunch.REQUEST_CODE -> {
                        if (paymentFlowListener != null) {
                            paymentFlowListener?.onCancelled()
                            paymentFlowListener = null
                        } else {
                            selectPaymentMethodFlowListener?.onCancelled()
                            selectPaymentMethodFlowListener = null
                        }
                        true
                    }
                    PaymentCheckoutActivityLaunch.REQUEST_CODE -> {
                        paymentDetailListener?.onCancelled()
                        paymentDetailListener = null
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
            PaymentShippingActivityLaunch.REQUEST_CODE,
            AddPaymentMethodActivityLaunch.REQUEST_CODE,
            PaymentCheckoutActivityLaunch.REQUEST_CODE
        )
    }
}
