package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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
    val paymentMethod: PaymentMethod? = null,

    /**
     * The payment method reference that you want to confirm
     */
    val paymentMethodReference: PaymentMethodReference? = null,

    /**
     * Options for payment method
     */
    val paymentMethodOptions: PaymentMethodOptions? = null,

    val device: Device? = null

) : AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_CUSTOMER_ID = "customer_id"
        private const val FIELD_PAYMENT_METHOD = "payment_method"
        private const val FIELD_PAYMENT_METHOD_REFERENCE = "payment_method_reference"
        private const val FIELD_PAYMENT_METHOD_OPTIONS = "payment_method_options"
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
                paymentMethod?.let {
                    mapOf(FIELD_PAYMENT_METHOD to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                paymentMethodReference?.let {
                    mapOf(FIELD_PAYMENT_METHOD_REFERENCE to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                paymentMethodOptions?.let {
                    mapOf(FIELD_PAYMENT_METHOD_OPTIONS to it.toParamMap())
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
        private var paymentMethod: PaymentMethod? = null
        private var paymentMethodReference: PaymentMethodReference? = null
        private var paymentMethodOptions: PaymentMethodOptions? = null
        private var device: Device? = null

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setDevice(device: Device?): Builder = apply {
            this.device = device
        }

        fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply {
            this.paymentMethod = paymentMethod
        }

        fun setPaymentMethodReference(paymentMethodReference: PaymentMethodReference?): Builder =
            apply {
                this.paymentMethodReference = paymentMethodReference
            }

        fun setPaymentMethodOptions(paymentMethodOptions: PaymentMethodOptions?): Builder =
            apply {
                this.paymentMethodOptions = paymentMethodOptions
            }

        override fun build(): PaymentIntentConfirmRequest {
            return PaymentIntentConfirmRequest(
                requestId = requestId,
                customerId = customerId,
                paymentMethod = paymentMethod,
                paymentMethodReference = paymentMethodReference,
                paymentMethodOptions = paymentMethodOptions,
                device = device
            )
        }
    }
}
