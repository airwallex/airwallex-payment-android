package com.airwallex.android.core.model

import android.os.Parcelable
import com.airwallex.android.core.model.parser.PaymentMethodReferenceParser
import kotlinx.parcelize.Parcelize

/**
 * Reference for payment method
 */
@Parcelize
data class PaymentMethodReference constructor(

    /**
     * The id of the [PaymentMethod]
     */
    val id: String,

    /**
     * The cvc of the card
     */
    val cvc: String
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>(
            PaymentMethodReferenceParser.FIELD_ID to id,
            PaymentMethodReferenceParser.FIELD_CVC to cvc
        )
    }
}
