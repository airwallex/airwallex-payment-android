package com.airwallex.android.model

import android.os.Parcelable
import androidx.annotation.IntRange
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PaymentIntent internal constructor(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("request_id")
    val requestId: String? = null,

    @SerializedName("amount")
    val amount: Float? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("merchant_order_id")
    val merchantOrderId: String? = null,

    @SerializedName("order")
    val order: PaymentIntentOrder? = null,

    @SerializedName("customer_id")
    val customerId: String? = null,

    @SerializedName("descriptor")
    val descriptor: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("captured_amount")
    val capturedAmount: Int? = null,

    @SerializedName("latest_payment_attempt")
    val latestPaymentAttempt: PaymentAttempt? = null,

    @SerializedName("available_payment_method_types")
    val availablePaymentMethodTypes: List<String>? = null,

    @SerializedName("customer_payment_methods")
    val customerPaymentMethods: List<String>? = null,

    @SerializedName("client_secret")
    val clientSecret: String? = null

) : AirwallexModel, Parcelable {

    @Parcelize
    data class PaymentIntentOrder internal constructor(

        @SerializedName("type")
        val type: String?,

        @SerializedName("products")
        val products: List<Product>?
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
        val capturedAmount: Int?,

        @SerializedName("refunded_amount")
        val refundedAmount: Int?,

        @SerializedName("created_at")
        val createdAt: Date?,

        @SerializedName("updated_at")
        val updatedAt: Date?,

        @SerializedName("device")
        val device: Device?

    ) : AirwallexModel, Parcelable
}