package com.airwallex.android.core

import android.content.Context
import android.os.Build
import com.airwallex.android.core.Airwallex.PaymentListener
import com.airwallex.android.core.model.*

interface PaymentManager {

    // Payment Intent
    /**
     * Continue the [PaymentIntent] using [Options], used for 3DS
     *
     * @param options contains the confirm [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun continuePaymentIntent(
        options: Options,
        listener: PaymentListener<PaymentIntent>
    )

    /**
     * Confirm the [PaymentIntent] using [Options]
     *
     * @param options contains the confirm [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun confirmPaymentIntent(
        options: Options,
        listener: PaymentListener<PaymentIntent>
    )

    /**
     * Retrieve the [PaymentIntent] using [Options]
     *
     * @param options contains the retrieve [PaymentIntent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun retrievePaymentIntent(
        options: Options,
        listener: PaymentListener<PaymentIntent>
    )

    // For 3DS 1.0
    /**
     * Retrieve paRes with id
     */
    fun retrieveParesWithId(
        options: Options,
        listener: PaymentListener<ThreeDSecurePares>
    )

    // Payment Method
    /**
     * Create a Airwallex [PaymentMethod] using [Options]
     *
     * @param options contains the create [PaymentMethod] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun createPaymentMethod(
        options: Options,
        listener: PaymentListener<PaymentMethod>
    )

    /**
     * Retrieve available payment method types
     *
     * @param options contains the retrieve [PaymentMethod] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun retrieveAvailablePaymentMethods(
        options: Options,
        listener: PaymentListener<AvailablePaymentMethodResponse>
    )

    // Payment Consent
    /**
     * Create a Airwallex [PaymentConsent] using [Options]
     *
     * @param options contains the create [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun createPaymentConsent(
        options: Options,
        listener: PaymentListener<PaymentConsent>
    )

    /**
     * Verify a Airwallex [PaymentConsent] using [Options]
     *
     * @param options contains the create [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun verifyPaymentConsent(
        options: Options,
        listener: PaymentListener<PaymentConsent>
    )

    /**
     * Disable [PaymentConsent] using [Options]
     *
     * @param options contains the retrieve [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun disablePaymentConsent(
        options: Options,
        listener: PaymentListener<PaymentConsent>
    )

    /**
     * Retrieve [PaymentConsent] using [Options]
     *
     * @param options contains the retrieve [PaymentConsent] params
     * @param listener a [PaymentListener] to receive the response or error
     */
    fun retrievePaymentConsent(
        options: Options,
        listener: PaymentListener<PaymentConsent>
    )

    companion object {
        private const val PLATFORM = "Android"
        private const val DEVICE_MODEL = "mobile"

        fun buildDeviceInfo(deviceId: String, applicationContext: Context): Device {
            return Device.Builder()
                .setDeviceId(deviceId)
                .setDeviceModel(DEVICE_MODEL)
                .setSdkVersion(AirwallexPlugins.getSdkVersion(applicationContext))
                .setPlatformType(PLATFORM)
                .setDeviceOS(Build.VERSION.RELEASE)
                .build()
        }
    }
}
