package com.airwallex.android

import android.app.Activity
import android.content.Intent
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import com.airwallex.android.view.SelectCurrencyActivityLaunch
import com.airwallex.android.view.ThreeDSecureActivityLaunch
import java.util.*

/**
 * Entry-point to the Airwallex SDK.
 */
class Airwallex internal constructor(
    private val paymentManager: PaymentManager
) {
    private val securityConnector: SecurityConnector by lazy {
        AirwallexSecurityConnector()
    }

    /**
     * Generic interface for an Airwallex API operation callback that either returns a [T], or an [Exception]
     */
    interface PaymentListener<T> {
        fun onFailed(exception: AirwallexException)
        fun onSuccess(response: T)
    }

    /**
     * Constructor of [Airwallex]
     */
    constructor() : this(
        AirwallexApiRepository()
    )

    private constructor(
        repository: ApiRepository
    ) : this(
        AirwallexPaymentManager(repository)
    )

    /**
     * Confirm a [PaymentIntent] by ID
     *
     * @param fragment the `Fragment` that is start confirm the payment intent
     * @param params [ConfirmPaymentIntentParams] used to confirm [PaymentIntent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun confirmPaymentIntent(
        fragment: Fragment,
        params: ConfirmPaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    ) {
        // Retrieve Device Fingerprinting
        securityConnector.retrieveSecurityToken(params.paymentIntentId, fragment.requireActivity().applicationContext,
            object : AirwallexSecurityConnector.SecurityTokenListener {
                override fun onResponse(deviceId: String) {
                    // Confirm PaymentIntent with Device Fingerprinting
                    paymentManager.confirmPaymentIntent(fragment.requireActivity(), deviceId, params, SelectCurrencyActivityLaunch(fragment.requireActivity()), ThreeDSecureActivityLaunch((fragment.requireActivity())), listener)
                }
            })
    }

    /**
     * Confirm a [PaymentIntent] by ID
     *
     * @param activity the `Activity` that is start confirm the payment intent
     * @param params [ConfirmPaymentIntentParams] used to confirm [PaymentIntent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun confirmPaymentIntent(
        activity: Activity,
        params: ConfirmPaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    ) {
        // Retrieve Device Fingerprinting
        securityConnector.retrieveSecurityToken(params.paymentIntentId, activity.applicationContext,
            object : AirwallexSecurityConnector.SecurityTokenListener {
                override fun onResponse(deviceId: String) {
                    // Confirm PaymentIntent with Device Fingerprinting
                    paymentManager.confirmPaymentIntent(activity, deviceId, params, SelectCurrencyActivityLaunch(activity), ThreeDSecureActivityLaunch(activity), listener)
                }
            })
    }

    /**
     * Continue a [PaymentIntent] - select your current currency
     *
     * @param activity the `Activity` that is start confirm the payment intent
     * @param params [ContinuePaymentIntentParams] used to continue [PaymentIntent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    internal fun continuePaymentIntent(
        activity: Activity,
        threeDSecureActivityLaunch: ThreeDSecureActivityLaunch,
        params: ContinuePaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    ) {
        val request = PaymentIntentContinueRequest(
            requestId = UUID.randomUUID().toString(),
            type = params.type,
            threeDSecure = params.threeDSecure,
            device = params.device,
            useDcc = params.useDcc
        )
        paymentManager.continueDccPaymentIntent(
            activity,
            threeDSecureActivityLaunch,
            AirwallexApiRepository.ContinuePaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId,
                request = request
            ),
            listener
        )
    }

    /**
     * Retrieve a [PaymentIntent] by ID
     *
     * @param params [RetrievePaymentIntentParams] used to receive the [PaymentIntent]
     * @param listener a [PaymentListener] to receive the response or error
     */
    @UiThread
    fun retrievePaymentIntent(
        params: RetrievePaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    ) {
        paymentManager.retrievePaymentIntent(
            AirwallexApiRepository.RetrievePaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId
            ),
            listener
        )
    }

    /**
     * Create a payment method
     *
     * @param params [CreatePaymentMethodParams] used to create the [PaymentMethod]
     * @param listener the callback of create [PaymentMethod]
     */
    @UiThread
    internal fun createPaymentMethod(
        params: CreatePaymentMethodParams,
        listener: PaymentListener<PaymentMethod>
    ) {
        paymentManager.createPaymentMethod(
            AirwallexApiRepository.CreatePaymentMethodOptions(
                clientSecret = params.clientSecret,
                customerId = params.customerId,
                request = PaymentMethodCreateRequest.Builder()
                    .setCustomerId(params.customerId)
                    .setRequestId(UUID.randomUUID().toString())
                    .setType(PaymentMethodType.CARD)
                    .setCard(params.card)
                    .setBilling(params.billing)
                    .build()
            ),
            listener
        )
    }

    /**
     * Retrieve payment methods
     *
     * @param params [RetrievePaymentMethodParams] used to retrieve the [PaymentMethod]
     * @param listener the callback of get [PaymentMethod]
     */
    @UiThread
    internal fun retrievePaymentMethods(
        params: RetrievePaymentMethodParams,
        listener: PaymentListener<PaymentMethodResponse>
    ) {
        paymentManager.retrievePaymentMethods(
            AirwallexApiRepository.RetrievePaymentMethodOptions(
                clientSecret = params.clientSecret,
                customerId = params.customerId,
                pageNum = params.pageNum,
                pageSize = params.pageSize,
                fromCreatedAt = params.fromCreatedAt,
                toCreatedAt = params.toCreatedAt,
                type = params.type
            ),
            listener
        )
    }

    fun onPaymentIntentResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SelectCurrencyActivityLaunch.REQUEST_CODE) {
            SelectCurrencyManager.handleOnActivityResult(data, resultCode)
        } else if (requestCode == ThreeDSecureActivityLaunch.REQUEST_CODE) {
            ThreeDSecureManager.handleOnActivityResult(data)
        }
    }

    companion object {
        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(configuration: AirwallexConfiguration) {
            AirwallexPlugins.initialize(configuration)
        }
    }
}
