package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
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

    /**
     * Unique identifier for the payment intent
     */
    @SerializedName("id")
    val id: String,

    /**
     * Unique request ID specified by the merchant in the last operation
     */
    @SerializedName("request_id")
    val requestId: String,

    /**
     * Payment amount. This is the order amount you would like to charge your customer
     */
    @SerializedName("amount")
    val amount: BigDecimal,

    /**
     * Amount currency
     */
    @SerializedName("currency")
    val currency: String,

    /**
     * The order ID created in merchant's order system that corresponds to this payment intent
     */
    @SerializedName("merchant_order_id")
    val merchantOrderId: String,

    /**
     * The purchase order info
     */
    @SerializedName("order")
    val order: PurchaseOrder,

    /**
     * The customer who is paying for this payment intent
     */
    @SerializedName("customer_id")
    val customerId: String?,

    /**
     * Descriptor that will be displayed to the customer
     */
    @SerializedName("descriptor")
    val descriptor: String?,

    /**
     * A set of key-value pairs that you can attach to this customer
     */
    @SerializedName("metadata")
    val metadata: Map<String, String>? = null,

    /**
     * The status for the payment intent
     */
    @SerializedName("status")
    val status: PaymentIntentStatus?,

    /**
     * Amount that captured from this payment intent
     */
    @SerializedName("captured_amount")
    val capturedAmount: BigDecimal,

    /**
     * Latest payment attempt that was created under the payment intent
     */
    @SerializedName("latest_payment_attempt")
    val latestPaymentAttempt: PaymentAttempt?,

    /**
     * Available payment method types
     */
    @SerializedName("available_payment_method_types")
    val availablePaymentMethodTypes: List<PaymentMethodType>? = null,

    /**
     * Payment methods of the customer if customer ID is provided
     */
    @SerializedName("customer_payment_methods")
    val customerPaymentMethods: List<PaymentMethod>? = null,

    /**
     * Client secret for browser or app
     */
    @SerializedName("client_secret")
    val clientSecret: String?,

    /**
     * Next action for the payment intent
     */
    @SerializedName("next_action")
    val nextAction: NextAction? = null,

    /**
     * Time at which this payment intent was created
     */
    @SerializedName("created_at")
    val createdAt: Date,

    /**
     * Last time at which this payment intent was updated or operated on
     */
    @SerializedName("updated_at")
    val updatedAt: Date,

    /**
     * Last time at which this payment intent was cancelled. Only present when the payment intent was successfully cancelled, i.e. status is CANCELLED
     */
    @SerializedName("cancelled_at")
    val cancelledAt: Date? = null,

    /**
     * Reason for cancelling the payment intent. Only present when the payment intent was successfully cancelled, i.e. status is CANCELLED
     */
    @SerializedName("cancellation_reason")
    val cancellationReason: String? = null

) : AirwallexModel, Parcelable {

    val paymentMethodType: PaymentMethodType?
        get() {
            if (latestPaymentAttempt == null) {
                return null
            }
            return latestPaymentAttempt.paymentMethod.type
        }

    val weChat: WeChat?
        get() {
            if (latestPaymentAttempt == null ||
                latestPaymentAttempt.paymentMethod.type != PaymentMethodType.WECHAT ||
                nextAction == null ||
                nextAction.type != NextActionType.CALL_SDK ||
                nextAction.data == null
            ) {
                return null
            }

            return WeChat(
                appId = nextAction.data["appId"] as? String,
                partnerId = nextAction.data["partnerId"] as? String,
                prepayId = nextAction.data["prepayId"] as? String,
                `package` = nextAction.data["package"] as? String,
                nonceStr = nextAction.data["nonceStr"] as? String,
                timestamp = nextAction.data["timeStamp"] as? String,
                sign = nextAction.data["sign"] as? String
            )
        }

    @Parcelize
    data class PaymentAttempt internal constructor(

        /**
         * Unique identifier for the payment attempt
         */
        @SerializedName("id")
        val id: String,

        /**
         * Payment amount. This is the order amount you would like to charge your customer
         */
        @SerializedName("amount")
        val amount: BigDecimal,

        /**
         * Currency of the captured and refunded amounts
         */
        @SerializedName("currency")
        val currency: String? = null,

        /**
         * Payment method used by the payment attempt
         */
        @SerializedName("payment_method")
        val paymentMethod: PaymentMethod,

        /**
         * Captured amount
         */
        @SerializedName("captured_amount")
        val capturedAmount: BigDecimal,

        /**
         * Refunded amount
         */
        @SerializedName("refunded_amount")
        val refundedAmount: BigDecimal,

        /**
         * Time at which this payment attempt was created
         */
        @SerializedName("created_at")
        val createdAt: Date,

        /**
         * Last time at which this payment attempt was updated or operated on
         */
        @SerializedName("updated_at")
        val updatedAt: Date,

        /**
         * Authentication data used by the payment attempt
         */
        @SerializedName("authentication_data")
        val authenticationData: PaymentAttemptAuthData?
    ) : AirwallexModel, Parcelable

    @Parcelize
    data class PaymentAttemptAuthData internal constructor(

        @SerializedName("ds_data")
        val dsData: PaymentAttemptAuthDSData?,

        @SerializedName("fraud_data")
        val fraudData: PaymentAttemptAuthFraudData?,

        @SerializedName("avs_result")
        val avsResult: String?,

        @SerializedName("cvc_result")
        val cvcResult: String?

    ) : AirwallexModel, Parcelable

    @Parcelize
    data class PaymentAttemptAuthDSData internal constructor(

        /**
         * 3DS Version
         */
        @SerializedName("version")
        val version: String?,

        @SerializedName("liability_shift_indicator")
        val liabilityShiftIndicator: String?,

        /**
         * The Eci.
         */
        @SerializedName("eci")
        val eci: String?,

        /**
         * The Cavv.
         */
        @SerializedName("cavv")
        val cavv: String?,

        /**
         * The Xid.
         */
        @SerializedName("xid")
        val xid: String?,

        /**
         * Status of Authentication eligibility.
         */
        @SerializedName("enrolled")
        val enrolled: String?,

        /**
         * Transaction status result identifier.
         */
        @SerializedName("pa_res_status")
        val paResStatus: String?,

        @SerializedName("challenge_cancellation_reason")
        val challengeCancellationReason: String?,

        @SerializedName("frictionless")
        val frictionless: String?
    ) : AirwallexModel, Parcelable

    @Parcelize
    data class PaymentAttemptAuthFraudData internal constructor(

        @SerializedName("action")
        val action: String?,

        @SerializedName("score")
        val score: String?

    ) : AirwallexModel, Parcelable

    @Parcelize
    data class NextAction internal constructor(

        /**
         * Type of next action, can be one of render_qr_code, call_sdk, redirect, display
         */
        @SerializedName("type")
        val type: NextActionType?,

        /**
         * The additional data that can be used to complete this action
         */
        @SerializedName("data")
        val data: @RawValue Map<String, Any>?,

        /**
         * The dcc data that can be used to complete this action
         */
        @SerializedName("dcc_data")
        val dcc: DccData?
    ) : AirwallexModel, Parcelable

    /**
     * The status of a [PaymentIntent]
     */
    @Parcelize
    data class DccData internal constructor(
        @SerializedName("currency")
        val currency: String?,

        @SerializedName("amount")
        val amount: BigDecimal?,

        @SerializedName("currency_pair")
        val currencyPair: String?,

        @SerializedName("client_rate")
        val clientRate: Double?,

        @SerializedName("rate_source")
        val rateSource: String?,

        @SerializedName("rate_timestamp")
        val rateTimestamp: String?,

        @SerializedName("rate_expiry")
        val rateExpiry: String?

    ) : AirwallexModel, Parcelable

    /**
     * The status of a [PaymentIntent]
     */
    @Parcelize
    enum class NextActionType : Parcelable {

        @SerializedName("render_qr_code")
        RENDER_QR_CODE,

        @SerializedName("call_sdk")
        CALL_SDK,

        @SerializedName("redirect")
        REDIRECT,

        @SerializedName("display")
        DISPLAY,

        @SerializedName("dcc")
        DCC
    }
}
