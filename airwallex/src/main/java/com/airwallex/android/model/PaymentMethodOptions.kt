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
         * Three ds for card options
         */
        @SerializedName("three_ds")
        val threeDs: ThreeDs?

    ) : AirwallexModel, Parcelable {

        class Builder : ObjectBuilder<CardOptions> {
            private var autoCapture: Boolean = true
            private var threeDs: ThreeDs? = null

            fun setAutoCapture(autoCapture: Boolean): Builder = apply {
                this.autoCapture = autoCapture
            }

            fun setThreeDs(threeDs: ThreeDs): Builder = apply {
                this.threeDs = threeDs
            }

            override fun build(): CardOptions {
                return CardOptions(
                    autoCapture = autoCapture,
                    threeDs = threeDs
                )
            }
        }

        @Parcelize
        data class ThreeDs internal constructor(

            /**
             * PA response
             */
            @SerializedName("pa_res")
            val paRes: String?,

            /**
             * Return url
             */
            @SerializedName("return_url")
            val returnUrl: String?,

            /**
             * Last attempt id
             */
            @SerializedName("attempt_id")
            val attemptId: String?,

            /**
             * Device data collection response
             */
            @SerializedName("device_data_collection_res")
            val deviceDataCollectionRes: String?,

            /**
             * 3DS transactionId
             */
            @SerializedName("ds_transaction_id")
            private var dsTransactionId: String?,

            /**
             * Enable three domain secure
             */
            @SerializedName("option")
            private var option: Boolean

        ) : AirwallexModel, Parcelable {

            class Builder : ObjectBuilder<ThreeDs> {
                private var paRes: String? = null

                private var returnUrl: String? = null

                private var attemptId: String? = null

                private var deviceDataCollectionRes: String? = null

                private var dsTransactionId: String? = null

                private var option: Boolean = false

                fun setPaRes(paRes: String?): Builder = apply {
                    this.paRes = paRes
                }

                fun setReturnUrl(returnUrl: String?): Builder = apply {
                    this.returnUrl = returnUrl
                }

                fun setAttemptId(attemptId: String?): Builder = apply {
                    this.attemptId = attemptId
                }

                fun setDeviceDataCollectionRes(deviceDataCollectionRes: String?): Builder = apply {
                    this.deviceDataCollectionRes = deviceDataCollectionRes
                }

                fun setDsTransactionId(dsTransactionId: String?): Builder = apply {
                    this.dsTransactionId = dsTransactionId
                }

                fun setOption(option: Boolean): Builder = apply {
                    this.option = option
                }

                override fun build(): ThreeDs {
                    return ThreeDs(
                        paRes = paRes,
                        returnUrl = returnUrl,
                        attemptId = attemptId,
                        deviceDataCollectionRes = deviceDataCollectionRes,
                        dsTransactionId = dsTransactionId,
                        option = option
                    )
                }
            }
        }
    }
}
