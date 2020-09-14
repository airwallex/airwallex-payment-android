package com.airwallex.android

import android.content.Context
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.airwallex.android.Airwallex.PaymentListener
import com.airwallex.android.model.*
import java.util.*

internal interface PaymentManager {

    /**
     * Continue the [PaymentIntent] using [ApiRepository.Options], used for 3DS
     *
     * @param options contains the confirm [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun continuePaymentIntent(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentIntent>
    )

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
     * @param device device info
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun handleNextAction(
        activity: FragmentActivity,
        params: ConfirmPaymentIntentParams,
        serverJwt: String,
        device: Device,
        listener: PaymentListener<PaymentIntent>
    )

    companion object {
        private const val PLATFORM = "Android"

        fun buildDeviceInfo(deviceId: String, applicationContext: Context): Device {
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
            device: Device
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
                .setDevice(device)
                .build()

            return AirwallexApiRepository.ConfirmPaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId,
                request = request
            )
        }

        fun buildCardPaymentIntentOptions(
            device: Device,
            params: ConfirmPaymentIntentParams,
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
                .setDevice(device)
                .setPaymentMethodReference(requireNotNull(params.paymentMethodReference))
            return AirwallexApiRepository.ConfirmPaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId,
                request = request.build()
            )
        }

        fun buildContinuePaymentIntentOptions(
            device: Device,
            params: ConfirmPaymentIntentParams,
            type: PaymentIntentContinueType,
            threeDSecure: PaymentMethodOptions.CardOptions.ThreeDSecure
        ): AirwallexApiRepository.ContinuePaymentIntentOptions {
            val request = PaymentIntentContinueRequest(
                requestId = UUID.randomUUID().toString(),
                type = type,
                threeDSecure = threeDSecure,
                device = device
            )
            return AirwallexApiRepository.ContinuePaymentIntentOptions(
                clientSecret = params.clientSecret,
                paymentIntentId = params.paymentIntentId,
                request = request
            )

        }
    }
}
