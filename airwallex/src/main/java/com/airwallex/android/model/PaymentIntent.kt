package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*

/**
 * A PaymentIntent guides you through the process of collecting a payment from your customer.
 * It tracks a payment when your customer first checks out, through to when payment is successfully
 * paid by your customer. A PaymentIntent transitions through a number of states throughout the
 * customer's payment journey.
 */
@Parcelize
data class PaymentIntent internal constructor(

    // Unique identifier for the payment intent
    @SerializedName("id")
    val id: String,

    // Unique request ID specified by the merchant in the last operation
    @SerializedName("request_id")
    val requestId: String,

    // Payment amount. This is the order amount you would like to charge your customer
    @SerializedName("amount")
    val amount: BigDecimal,

    // Amount currency
    @SerializedName("currency")
    val currency: String,

    // The order ID created in merchant's order system that corresponds to this payment intent
    @SerializedName("merchant_order_id")
    val merchantOrderId: String,

    // The purchase order info
    @SerializedName("order")
    val order: PurchaseOrder,

    // The customer who is paying for this payment intent
    @SerializedName("customer_id")
    val customerId: String?,

    // Descriptor that will be displayed to the customer
    @SerializedName("descriptor")
    val descriptor: String?,

    // A set of key-value pairs that you can attach to this customer
    @SerializedName("metadata")
    val metadata: Map<String, String>? = null,

    @SerializedName("status")
    val status: PaymentIntentStatus,

    // Amount that captured from this payment intent
    @SerializedName("captured_amount")
    val capturedAmount: BigDecimal,

    // Latest payment attempt that was created under the payment intent
    @SerializedName("latest_payment_attempt")
    val latestPaymentAttempt: PaymentAttempt? = null,

    @SerializedName("available_payment_method_types")
    val availablePaymentMethodTypes: List<String>,

    // Payment methods of the customer if customer ID is provided
    @SerializedName("customer_payment_methods")
    val customerPaymentMethods: List<PaymentMethod>? = null,

    // Client secret for browser or app
    @SerializedName("client_secret")
    val clientSecret: String,

    @SerializedName("next_action")
    val nextAction: NextAction? = null,

    // Time at which this payment intent was created
    @SerializedName("created_at")
    val createdAt: Date? = null,

    // Last time at which this payment intent was updated or operated on
    @SerializedName("updated_at")
    val updatedAt: Date? = null,

    // Last time at which this payment intent was cancelled. Only present when the payment intent was successfully cancelled, i.e. status is CANCELLED
    @SerializedName("cancelled_at")
    val cancelledAt: Date? = null,

    // Reason for cancelling the payment intent. Only present when the payment intent was successfully cancelled, i.e. status is CANCELLED
    @SerializedName("cancellation_reason")
    val cancellationReason: String? = null

) : AirwallexModel, Parcelable {

    @Parcelize
    data class PaymentAttempt internal constructor(

        @SerializedName("id")
        val id: String,

        @SerializedName("currency")
        val currency: String,

        @SerializedName("payment_method")
        val paymentMethod: PaymentMethod,

        @SerializedName("status")
        val status: String,

        @SerializedName("captured_amount")
        val capturedAmount: BigDecimal,

        @SerializedName("refunded_amount")
        val refundedAmount: BigDecimal,

        @SerializedName("created_at")
        val createdAt: Date,

        @SerializedName("updated_at")
        val updatedAt: Date,

        @SerializedName("device")
        val device: Device?

    ) : AirwallexModel, Parcelable

    @Parcelize
    data class NextAction internal constructor(

        @SerializedName("type")
        val type: String,

        @SerializedName("data")
        val data: NextActionData?
    ) : AirwallexModel, Parcelable

    @Parcelize
    data class NextActionData internal constructor(

        @SerializedName("appId")
        val appId: String?,

        @SerializedName("timeStamp")
        val timeStamp: String?,

        @SerializedName("nonceStr")
        val nonceStr: String?,

        @SerializedName("prepayId")
        val prepayId: String?,

        @SerializedName("partnerId")
        val partnerId: String?,

        @SerializedName("package")
        val packageValue: String?,

        @SerializedName("sign")
        val sign: String?,

        @SerializedName("jwt")
        val jwt: String?
    ) : AirwallexModel, Parcelable
}
