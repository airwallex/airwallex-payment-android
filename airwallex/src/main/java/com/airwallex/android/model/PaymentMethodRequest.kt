package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.CurrencyUtils
import com.airwallex.android.model.parser.PaymentMethodParser
import kotlinx.parcelize.Parcelize

@Parcelize
class PaymentMethodRequest(

    /**
     * Unique identifier for the payment method
     */
    val id: String? = null,

    /**
     * Type of the payment method
     */
    val type: PaymentMethodType,

    /**
     * Redirect Request
     */
    val redirectRequest: RedirectRequest? = null

) : AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                id?.let {
                    mapOf(PaymentMethodParser.FIELD_ID to it)
                }.orEmpty()
            )
            .plus(
                mapOf(PaymentMethodParser.FIELD_TYPE to type.value)
            )
            .plus(
                redirectRequest?.let {
                    mapOf(type.value to it.toParamMap())
                }.orEmpty()
            )
    }

    class Builder(
        val type: PaymentMethodType
    ) : ObjectBuilder<PaymentMethodRequest> {
        private var redirectRequest: RedirectRequest? = null

        fun setPaymentMethodRequest(
            type: PaymentMethodType,
            name: String?,
            email: String?,
            phone: String?,
            currency: String?,
            bank: Bank?
        ): Builder = apply {
            when (type.classify) {
                PaymentMethodClassify.WECHAT,
                PaymentMethodClassify.REDIRECT -> redirectRequest = RedirectRequest(
                    bank = bank,
                    name = name,
                    email = email,
                    phone = phone,
                    countryCode = currency?.let {
                        CurrencyUtils.currencyToCountryMap[currency]
                    }
                )
                else -> Unit
            }
        }

        override fun build(): PaymentMethodRequest {
            return PaymentMethodRequest(
                type = type,
                redirectRequest = redirectRequest
            )
        }
    }
}
