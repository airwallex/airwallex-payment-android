package com.airwallex.android.model

import android.os.Parcelable
import androidx.annotation.IntRange
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

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
    val merchantOrderId: String,

    @SerializedName("order")
    val order: PaymentIntentOrder,

    @SerializedName("customer_id")
    val customerId: String,

    @SerializedName("descriptor")
    val descriptor: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("captured_amount")
    val capturedAmount: Int,

    @SerializedName("latest_payment_attempt")
    val latestPaymentAttempt: PaymentAttempt,

    @SerializedName("available_payment_method_types")
    val availablePaymentMethodTypes: List<String>,

    @SerializedName("customer_payment_methods")
    val customerPaymentMethods: List<String>,

    @SerializedName("client_secret")
    val clientSecret: String

) : AirwallexModel, Parcelable {

    @Parcelize
    data class PaymentIntentOrder internal constructor(

        @SerializedName("type")
        val type: String,

        @SerializedName("products")
        val products: List<Product>
    ) : AirwallexModel, Parcelable

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
        val capturedAmount: Int,

        @SerializedName("refunded_amount")
        val refundedAmount: Int,

        @SerializedName("created_at")
        val createdAt: Date,

        @SerializedName("updated_at")
        val updatedAt: Date,

        @SerializedName("device")
        val device: Device

    ) : AirwallexModel, Parcelable
}