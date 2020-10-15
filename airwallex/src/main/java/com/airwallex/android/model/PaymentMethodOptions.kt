package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Options for payment method
 */
@Parcelize
data class PaymentMethodOptions internal constructor(

    /**
     * The payment method options for card
     */
    @SerializedName("card")
    val cardOptions: CardOptions? = null
) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<PaymentMethodOptions> {
        private var cardOptions: CardOptions? = null

        fun setCardOptions(cardOptions: CardOptions?): Builder = apply {
            this.cardOptions = cardOptions
        }

        override fun build(): PaymentMethodOptions {
            return PaymentMethodOptions(
                cardOptions = cardOptions
            )
        }
    }

    @Parcelize
    data class CardOptions internal constructor(

        /**
         * Should capture automatically when confirm. Default to false. The payment intent will be captured automatically if it is true, and authorized only if it is false
         */
        @SerializedName("auto_capture")
        val autoCapture: Boolean,

        /**
         * 3D Secure for card options
         */
        @SerializedName("three_ds")
        val threeDSecure: ThreeDSecure?

    ) : AirwallexModel, Parcelable {

        class Builder : ObjectBuilder<CardOptions> {
            private var autoCapture: Boolean = true
            private var threeDSecure: ThreeDSecure? = null

            fun setAutoCapture(autoCapture: Boolean): Builder = apply {
                this.autoCapture = autoCapture
            }

            fun setThreeDSecure(threeDSecure: ThreeDSecure): Builder = apply {
                this.threeDSecure = threeDSecure
            }

            override fun build(): CardOptions {
                return CardOptions(
                    autoCapture = autoCapture,
                    threeDSecure = threeDSecure
                )
            }
        }

        @Parcelize
        data class ThreeDSecure internal constructor(

            /**
             * Return url for 3D Secure
             */
            @SerializedName("return_url")
            val returnUrl: String?,

            /**
             * Device data collection response for 3D Secure
             */
            @SerializedName("device_data_collection_res")
            val deviceDataCollectionRes: String?,

            /**
             * Transaction ID for 3D Secure
             */
            @SerializedName("ds_transaction_id")
            private var transactionId: String?

        ) : AirwallexModel, Parcelable {

            class Builder : ObjectBuilder<ThreeDSecure> {
                private var returnUrl: String? = null

                private var deviceDataCollectionRes: String? = null

                private var transactionId: String? = null

                fun setReturnUrl(returnUrl: String?): Builder = apply {
                    this.returnUrl = returnUrl
                }

                fun setDeviceDataCollectionRes(deviceDataCollectionRes: String?): Builder = apply {
                    this.deviceDataCollectionRes = deviceDataCollectionRes
                }

                fun setTransactionId(transactionId: String?): Builder = apply {
                    this.transactionId = transactionId
                }

                override fun build(): ThreeDSecure {
                    return ThreeDSecure(
                        returnUrl = returnUrl,
                        deviceDataCollectionRes = deviceDataCollectionRes,
                        transactionId = transactionId
                    )
                }
            }
        }
    }
}
