package com.airwallex.android

import android.content.Context
import android.os.Build
import com.airwallex.android.Airwallex.PaymentListener
import com.airwallex.android.model.*
import com.airwallex.android.view.DccActivityLaunch
import com.airwallex.android.view.ThreeDSecureActivityLaunch
import java.util.*

internal interface PaymentManager {

    var dccCallback: DccCallback?
    var threeDSecureCallback: ThreeDSecureCallback?

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

    /**
     * Retrieve paRes with id
     */
    fun retrieveParesWithId(
        options: ApiRepository.Options,
        listener: PaymentListener<ThreeDSecurePares>
    )

    /**
     * Confirm [PaymentIntent] with device id
     */
    fun confirmPaymentIntent(
        applicationContext: Context,
        deviceId: String,
        params: ConfirmPaymentIntentParams,
        selectCurrencyActivityLaunch: DccActivityLaunch,
        threeDSecureActivityLaunch: ThreeDSecureActivityLaunch,
        listener: PaymentListener<PaymentIntent>
    )

    /**
     * Continue [PaymentIntent] with your selected currency
     */
    fun continueDccPaymentIntent(
        applicationContext: Context,
        threeDSecureActivityLaunch: ThreeDSecureActivityLaunch,
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentIntent>
    )

    /**
     * Create a Airwallex [PaymentConsent] using [ApiRepository.Options]
     *
     * @param options contains the create [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun createPaymentConsent(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentConsent>
    )

    /**
     * Verify a Airwallex [PaymentConsent] using [ApiRepository.Options]
     *
     * @param options contains the create [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun verifyPaymentConsent(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentConsent>
    )

    /**
     * Retrieve [PaymentConsent] using [ApiRepository.Options]
     *
     * @param options contains the retrieve [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun retrievePaymentConsent(
        options: ApiRepository.Options,
        listener: PaymentListener<PaymentConsent>
    )

    /**
     * Handle next action for 3ds
     *
     * @param applicationContext the Application Context that is to start 3ds screen
     * @param threeDSecureActivityLaunch instance of [ThreeDSecureActivityLaunch]
     * @param paymentIntentId the ID of the [PaymentIntent], required.
     * @param clientSecret the clientSecret of [PaymentIntent], required.
     * @param serverJwt for perform 3ds flow
     * @param device device info
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun handle3DSFlow(
        applicationContext: Context,
        threeDSecureActivityLaunch: ThreeDSecureActivityLaunch,
        paymentIntentId: String,
        clientSecret: String,
        serverJwt: String,
        device: Device?,
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

        fun buildCardPaymentIntentOptions(
            device: Device,
            params: ConfirmPaymentIntentParams,
            threeDSecure: ThreeDSecure
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
                        .setWeChatPayRequest(WeChatPayRequest(ThirdPartPayRequestFlow.IN_APP))
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

        fun buildAliPayCnPaymentIntentOptions(
            params: ConfirmPaymentIntentParams,
            device: Device
        ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
            val request = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethod(
                    PaymentMethod.Builder()
                        .setType(PaymentMethodType.ALIPAY_CN)
                        .setAliPayCNRequest(AliPayRequest(ThirdPartPayRequestFlow.IN_APP))
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

        fun buildAliPayHkPaymentIntentOptions(
            params: ConfirmPaymentIntentParams,
            device: Device
        ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
            val request = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethod(
                    PaymentMethod.Builder()
                        .setType(PaymentMethodType.ALIPAY_HK)
                        .setAliPayHkRequest(AliPayRequest(ThirdPartPayRequestFlow.IN_APP))
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

        fun buildKakaoPayPaymentIntentOptions(
            params: ConfirmPaymentIntentParams,
            device: Device
        ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
            val request = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethod(
                    PaymentMethod.Builder()
                        .setType(PaymentMethodType.KAKAOPAY)
                        .setKakaoPayRequest(AliPayRequest(ThirdPartPayRequestFlow.IN_APP))
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

        fun buildTngPaymentIntentOptions(
            params: ConfirmPaymentIntentParams,
            device: Device
        ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
            val request = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethod(
                    PaymentMethod.Builder()
                        .setType(PaymentMethodType.TNG)
                        .setTngRequest(AliPayRequest(ThirdPartPayRequestFlow.IN_APP))
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

        fun buildDanaPaymentIntentOptions(
            params: ConfirmPaymentIntentParams,
            device: Device
        ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
            val request = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethod(
                    PaymentMethod.Builder()
                        .setType(PaymentMethodType.DANA)
                        .setDanaRequest(AliPayRequest(ThirdPartPayRequestFlow.IN_APP))
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

        fun buildGCashPaymentIntentOptions(
            params: ConfirmPaymentIntentParams,
            device: Device
        ): AirwallexApiRepository.ConfirmPaymentIntentOptions {
            val request = PaymentIntentConfirmRequest.Builder(
                requestId = UUID.randomUUID().toString()
            )
                .setPaymentMethod(
                    PaymentMethod.Builder()
                        .setType(PaymentMethodType.GCASH)
                        .setGCashRequest(AliPayRequest(ThirdPartPayRequestFlow.IN_APP))
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
    }
}
