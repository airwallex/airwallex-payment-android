package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethodOptions internal constructor(

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

        @SerializedName("auto_capture")
        val autoCapture: Boolean,

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

            @SerializedName("option")
            val option: Boolean

        ) : AirwallexModel, Parcelable {

            class Builder : ObjectBuilder<ThreeDs> {
                private var option: Boolean = false

                fun setOption(option: Boolean): Builder = apply {
                    this.option = option
                }

                override fun build(): ThreeDs {
                    return ThreeDs(
                        option = option
                    )
                }
            }
        }
    }


}