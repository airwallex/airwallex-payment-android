package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * The request params to confirm [PaymentIntent]
 */
@Parcelize
data class PaymentIntentConfirmRequest internal constructor(

    /**
     * Unique request ID specified by the merchant
     */
    @SerializedName("request_id")
    val requestId: String,

    /**
     * Customer who intends to pay for the payment intent
     */
    @SerializedName("customer_id")
    val customerId: String? = null,

    /**
     * The payment method that you want to confirm
     */
    @SerializedName("payment_method")
    val paymentMethod: PaymentMethod?,

    /**
     * The payment method reference that you want to confirm
     */
    @SerializedName("payment_method_reference")
    val paymentMethodReference: PaymentMethodReference?,

    /**
     * Options for payment method
     */
    @SerializedName("payment_method_options")
    val paymentMethodOptions: PaymentMethodOptions?,

    @SerializedName("device")
    val device: Device? = null

) : AirwallexModel, Parcelable {

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
