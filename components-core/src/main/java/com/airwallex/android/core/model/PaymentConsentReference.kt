package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Reference to an existing PaymentConsent
 */
@Parcelize
data class PaymentConsentReference internal constructor(

    /**
     * ID of the PaymentConsent referenced for this recurring payment
     */
    val id: String? = null,

    /**
     * When requires_cvc of the PaymentConsent is true, this attribute must be provided in order to confirm successfully
     */
    val cvc: String? = null

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_ID = "id"
        private const val FIELD_CVC = "cvc"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                id?.let {
                    mapOf(FIELD_ID to it)
                }.orEmpty()
            )
            .plus(
                cvc?.let {
                    mapOf(FIELD_CVC to it)
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<PaymentConsentReference> {
        private var id: String? = null
        private var cvc: String? = null

        fun setId(id: String?): Builder = apply {
            this.id = id
        }

        fun setCvc(cvc: String?): Builder = apply {
            this.cvc = cvc
        }

        override fun build(): PaymentConsentReference {
            return PaymentConsentReference(
                id = id,
                cvc = cvc
            )
        }
    }
}
