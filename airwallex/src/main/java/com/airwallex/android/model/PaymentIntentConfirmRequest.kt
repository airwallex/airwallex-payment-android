package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentIntentConfirmRequest internal constructor(

    // Unique request ID specified by the merchant
    @SerializedName("request_id")
    val requestId: String? = null,

    // Customer who intends to pay for the payment intent
    @SerializedName("customer_id")
    val customerId: String? = null,

    @SerializedName("payment_method")
    val paymentMethod: PaymentMethod? = null,

    @SerializedName("device")
    val device: Device? = null,

    @SerializedName("payment_method_reference")
    val paymentMethodReference: PaymentMethodReference?,

    @SerializedName("payment_method_options")
    val paymentMethodOptions: PaymentMethodOptions?

) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<PaymentIntentConfirmRequest> {
        private var requestId: String? = null
        private var customerId: String? = null
        private var paymentMethod: PaymentMethod? = null
        private var device: Device? = null
        private var paymentMethodReference: PaymentMethodReference? = null
        private var paymentMethodOptions: PaymentMethodOptions? = null

        fun setRequestId(requestId: String?): Builder = apply {
            this.requestId = requestId
        }

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply {
            this.paymentMethod = paymentMethod
        }

        fun setDevice(device: Device?): Builder = apply {
            this.device = device
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
