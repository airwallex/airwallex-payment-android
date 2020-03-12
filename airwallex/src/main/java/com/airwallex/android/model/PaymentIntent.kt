package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentIntent internal constructor(

    @SerializedName("id")
    val id: String,

    @SerializedName("request_id")
    val requestId: String,

    @SerializedName("amount")
    val amount: Float,

    @SerializedName("currency")
    val currency: String,

    @SerializedName("merchant_order_id")
    val merchantOrderId: String? = null,

    @SerializedName("order")
    val order: PaymentIntentOrder,

    @SerializedName("customer_id")
    val customerId: String?,

    @SerializedName("descriptor")
    val descriptor: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("captured_amount")
    val capturedAmount: Float? = null,

    @SerializedName("latest_payment_attempt")
    val latestPaymentAttempt: PaymentAttempt? = null,

    @SerializedName("available_payment_method_types")
    val availablePaymentMethodTypes: List<String>,

    @SerializedName("customer_payment_methods")
    val customerPaymentMethods: List<PaymentMethod>? = null,

    @SerializedName("client_secret")
    val clientSecret: String?,

    @SerializedName("next_action")
    val nextAction: NextAction? = null
) : AirwallexModel, Parcelable {

    @Parcelize
    data class PaymentIntentOrder internal constructor(

        @SerializedName("type")
        val type: String?,

        @SerializedName("products")
        val products: List<Product>? = null,

        @SerializedName("shipping")
        val shipping: Shipping? = null
    ) : AirwallexModel, Parcelable

    @Parcelize
    data class PaymentAttempt internal constructor(

        @SerializedName("id")
        val id: String?,

        @SerializedName("currency")
        val currency: String?,

        @SerializedName("payment_method")
        val paymentMethod: PaymentMethod?,

        @SerializedName("status")
        val status: String?,

        @SerializedName("captured_amount")
        val capturedAmount: Float?,

        @SerializedName("refunded_amount")
        val refundedAmount: Float?,

        @SerializedName("created_at")
        val createdAt: Date?,

        @SerializedName("updated_at")
        val updatedAt: Date?,

        @SerializedName("device")
        val device: Device?

    ) : AirwallexModel, Parcelable

    @Parcelize
    data class NextAction internal constructor(

        @SerializedName("type")
        val type: String?,

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
