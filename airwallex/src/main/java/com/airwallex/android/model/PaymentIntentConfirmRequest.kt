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
    val paymentMethod: PaymentMethod,

    /**
     * The user device info
     */
    @SerializedName("device")
    val device: Device,

    /**
     * The payment method reference that you want to confirm
     */
    @SerializedName("payment_method_reference")
    val paymentMethodReference: PaymentMethodReference?,

    /**
     * Options for payment method
     */
    @SerializedName("payment_method_options")
    val paymentMethodOptions: PaymentMethodOptions?

) : AirwallexModel, Parcelable {

    class Builder(
        private val requestId: String,
        private val paymentMethod: PaymentMethod,
        private val device: Device
    ) : ObjectBuilder<PaymentIntentConfirmRequest> {
        private var customerId: String? = null
        private var paymentMethodReference: PaymentMethodReference? = null
        private var paymentMethodOptions: PaymentMethodOptions? = null

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
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
                paymentMethod = paymentMethod,
                device = device,
                paymentMethodReference = paymentMethodReference,
                paymentMethodOptions = paymentMethodOptions
            )
        }
    }
}
