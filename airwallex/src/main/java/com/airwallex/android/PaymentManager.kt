package com.airwallex.android

import android.content.Context
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.airwallex.android.Airwallex.PaymentListener
import com.airwallex.android.model.*
import java.util.*

internal interface PaymentManager {

    /**
     * Confirm the [PaymentIntent] using [ApiRepository.Options]
     *
     * @param options contains the confirm [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun confirmPaymentIntent(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentIntent>
    )

    /**
     * Retrieve the [PaymentIntent] using [ApiRepository.Options]
     *
     * @param options contains the retrieve [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun retrievePaymentIntent(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentIntent>
    )

    /**
     * Create a Airwallex [PaymentMethod] using [ApiRepository.Options]
     *
     * @param options contains the create [PaymentMethod] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun createPaymentMethod(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentMethod>
    )

    /**
     * Retrieve all of the customer's [PaymentMethod] using [ApiRepository.Options]
     *
     * @param options contains the retrieve [PaymentMethod] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun retrievePaymentMethods(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentMethodResponse>
    )

    fun confirmPaymentIntentWithDeviceId(
        activity: FragmentActivity,
        deviceId: String,
        params: ConfirmPaymentIntentParams,
        listener: PaymentListener<PaymentIntent>
    )

    /**
     * Handle next action for 3ds
     *
     * @param activity the `Activity` that is to start 3ds screen
     * @param params [ConfirmPaymentIntentParams] used to confirm [PaymentIntent]
     * @param serverJwt for perform 3ds flow
     * @param deviceId device id
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun handleNextAction(
        activity: FragmentActivity,
        params: ConfirmPaymentIntentParams,
        serverJwt: String,
        deviceId: String,
        listener: PaymentListener<PaymentIntent>
    )

    companion object {
        private const val PLATFORM = "Android"

        private fun buildDeviceInfo(deviceId: String, applicationContext: Context): Device {
            return Device.Builder()
                .setDeviceId(deviceId)
                .setDeviceModel(Build.MODEL)
                .setSdkVersion(AirwallexPlugins.getSdkVersion(applicationContext))
                .setPlatformType(PLATFORM)
                .setDeviceOS(Build.VERSION.RELEASE)
                .build()
        }

        fun buildWeChatPaymentIntentOptions(
            params: ConfirmPaymentIntentParams,
            deviceId: String,
            applicationContext: Context
        ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
            val request = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethod(
                    PaymentMethod.Builder()
                        .setType(PaymentMethodType.WECHAT)
                        .setWeChatPayFlow(WeChatPayRequest(WeChatPayRequestFlow.IN_APP))
                        .build()
                )
                .setCustomerId(params.customerId)
                .setDevice(buildDeviceInfo(deviceId, applicationContext))
                .build()

            return AirwallexApiRepository.ConfirmPaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId,
                request = request
            )
        }

        fun buildCardPaymentIntentOptions(
            applicationContext: Context,
            deviceId: String,
            params: ConfirmPaymentIntentParams,
            withPaymentMethod: Boolean,
            threeDSecure: PaymentMethodOptions.CardOptions.ThreeDSecure
        ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
            val request = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethodOptions(
                    PaymentMethodOptions.Builder()
                        .setCardOptions(
                            PaymentMethodOptions.CardOptions.Builder()
                                .setAutoCapture(true)
                                .setThreeDSecure(threeDSecure).build()
                        )
                        .build()
                )
                .setCustomerId(params.customerId)
                .setDevice(buildDeviceInfo(deviceId, applicationContext))

            if (withPaymentMethod) {
                request.setPaymentMethodReference(requireNotNull(params.paymentMethodReference))
            }
            return AirwallexApiRepository.ConfirmPaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId,
                request = request.build()
            )
        }
    }
}
