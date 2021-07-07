package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The request params to confirm [PaymentIntent]
 */
@Parcelize
data class PaymentIntentConfirmRequest internal constructor(

    /**
     * Unique request ID specified by the merchant
     */
    val requestId: String? = null,

    /**
     * Customer who intends to pay for the payment intent
     */
    val customerId: String? = null,

    /**
     * The payment method that you want to confirm
     */
    val paymentMethodRequest: PaymentMethodRequest? = null,

    /**
     * Options for payment method
     */
    val paymentMethodOptions: PaymentMethodOptions? = null,

    /**
     * Reference to an existing PaymentConsent
     */
    val paymentConsentReference: PaymentConsentReference? = null,

    val device: Device? = null

) : AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_CUSTOMER_ID = "customer_id"
        private const val FIELD_PAYMENT_METHOD = "payment_method"
        private const val FIELD_PAYMENT_METHOD_OPTIONS = "payment_method_options"
        private const val FIELD_PAYMENT_CONSENT_REFERENCE = "payment_consent_reference"
        private const val FIELD_DEVICE = "device"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                requestId?.let {
                    mapOf(FIELD_REQUEST_ID to it)
                }.orEmpty()
            )
            .plus(
                customerId?.let {
                    mapOf(FIELD_CUSTOMER_ID to it)
                }.orEmpty()
            )
            .plus(
                paymentMethodRequest?.let {
                    mapOf(FIELD_PAYMENT_METHOD to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                paymentMethodOptions?.let {
                    mapOf(FIELD_PAYMENT_METHOD_OPTIONS to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                paymentConsentReference?.let {
                    mapOf(FIELD_PAYMENT_CONSENT_REFERENCE to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                device?.let {
                    mapOf(FIELD_DEVICE to it.toParamMap())
                }.orEmpty()
            )
    }

    class Builder(
        private val requestId: String
    ) : ObjectBuilder<PaymentIntentConfirmRequest> {
        private var customerId: String? = null
        private var paymentMethodRequest: PaymentMethodRequest? = null
        private var paymentMethodOptions: PaymentMethodOptions? = null
        private var paymentConsentReference: PaymentConsentReference? = null
        private var device: Device? = null

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setDevice(device: Device?): Builder = apply {
            this.device = device
        }

        fun setPaymentMethodRequest(paymentMethodRequest: PaymentMethodRequest?): Builder = apply {
            this.paymentMethodRequest = paymentMethodRequest
        }

        fun setPaymentMethodOptions(paymentMethodOptions: PaymentMethodOptions?): Builder =
            apply {
                this.paymentMethodOptions = paymentMethodOptions
            }

        fun setPaymentConsentReference(paymentConsentReference: PaymentConsentReference?): Builder =
            apply {
                this.paymentConsentReference = paymentConsentReference
            }

        override fun build(): PaymentIntentConfirmRequest {
            return PaymentIntentConfirmRequest(
                requestId = requestId,
                customerId = customerId,
                paymentMethodRequest = paymentMethodRequest,
                paymentMethodOptions = paymentMethodOptions,
                paymentConsentReference = paymentConsentReference,
                device = device
            )
        }
    }
}
