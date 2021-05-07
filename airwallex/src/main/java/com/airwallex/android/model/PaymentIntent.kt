package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
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
    val id: String,

    /**
     * Unique request ID specified by the merchant in the last operation
     */
    val requestId: String? = null,

    /**
     * Payment amount. This is the order amount you would like to charge your customer
     */
    val amount: BigDecimal,

    /**
     * Amount currency
     */
    val currency: String,

    /**
     * The order ID created in merchant's order system that corresponds to this payment intent
     */
    val merchantOrderId: String? = null,

    /**
     * The purchase order info
     */
    val order: PurchaseOrder? = null,

    /**
     * The customer who is paying for this payment intent
     */
    val customerId: String? = null,

    /**
     * Descriptor that will be displayed to the customer
     */
    val descriptor: String? = null,

    /**
     * A set of key-value pairs that you can attach to this customer
     */
    val metadata: Map<String, @RawValue Any?>? = null,

    /**
     * The status for the payment intent
     */
    val status: PaymentIntentStatus? = null,

    /**
     * Amount that captured from this payment intent
     */
    val capturedAmount: BigDecimal? = null,

    /**
     * Latest payment attempt that was created under the payment intent
     */
    val latestPaymentAttempt: PaymentAttempt? = null,

    /**
     * Available payment method types
     */
    val availablePaymentMethodTypes: List<AvaliablePaymentMethodType>? = null,

    /**
     * Payment methods of the customer if customer ID is provided
     */
    val customerPaymentMethods: List<PaymentMethod>? = null,

    /**
     * Client secret for browser or app
     */
    val clientSecret: String? = null,

    /**
     * Next action for the payment intent
     */
    val nextAction: NextAction? = null,

    /**
     * Time at which this payment intent was created
     */
    val createdAt: Date? = null,

    /**
     * Last time at which this payment intent was updated or operated on
     */
    val updatedAt: Date? = null,

    /**
     * Last time at which this payment intent was cancelled. Only present when the payment intent was successfully cancelled, i.e. status is CANCELLED
     */
    val cancelledAt: Date? = null,

    /**
     * Reason for cancelling the payment intent. Only present when the payment intent was successfully cancelled, i.e. status is CANCELLED
     */
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
                nextAction.type != NextAction.NextActionType.CALL_SDK ||
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
        val id: String?,

        /**
         * Payment amount. This is the order amount you would like to charge your customer
         */
        val amount: BigDecimal?,

        /**
         * Currency of the captured and refunded amounts
         */
        val currency: String? = null,

        /**
         * Payment method used by the payment attempt
         */
        val paymentMethod: PaymentMethod,

        /**
         * Captured amount
         */
        val capturedAmount: BigDecimal?,

        /**
         * Refunded amount
         */
        val refundedAmount: BigDecimal?,

        /**
         * Time at which this payment attempt was created
         */
        val createdAt: Date?,

        /**
         * Last time at which this payment attempt was updated or operated on
         */
        val updatedAt: Date?,

        /**
         * Authentication data used by the payment attempt
         */
        val authenticationData: PaymentAttemptAuthData?
    ) : AirwallexModel, Parcelable

    @Parcelize
    data class PaymentAttemptAuthData internal constructor(

        val dsData: PaymentAttemptAuthDSData?,

        val fraudData: PaymentAttemptAuthFraudData?,

        val avsResult: String?,

        val cvcResult: String?

    ) : AirwallexModel, Parcelable

    @Parcelize
    data class PaymentAttemptAuthDSData internal constructor(

        /**
         * 3DS Version
         */
        val version: String?,

        val liabilityShiftIndicator: String?,

        /**
         * The Eci.
         */
        val eci: String?,

        /**
         * The Cavv.
         */
        val cavv: String?,

        /**
         * The Xid.
         */
        val xid: String?,

        /**
         * Status of Authentication eligibility.
         */
        val enrolled: String?,

        /**
         * Transaction status result identifier.
         */
        val paResStatus: String?,

        val challengeCancellationReason: String?,

        val frictionless: String?
    ) : AirwallexModel, Parcelable

    @Parcelize
    data class PaymentAttemptAuthFraudData internal constructor(

        val action: String?,

        val score: String?

    ) : AirwallexModel, Parcelable
}
