package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethodReference internal constructor(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("cvc")
    val cvc: String? = null
) : AirwallexModel, Parcelable {

    class Builder : ObjectBuilder<PaymentMethodReference> {
        private var id: String? = null
        private var cvc: String? = null

        fun setId(id: String?): Builder = apply {
            this.id = id
        }

        fun setCvc(cvc: String?): Builder = apply {
            this.cvc = cvc
        }

        override fun build(): PaymentMethodReference {
            return PaymentMethodReference(
                id = id,
                cvc = cvc
            )
        }
    }
}
