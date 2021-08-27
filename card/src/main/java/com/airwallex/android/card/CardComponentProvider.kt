package com.airwallex.android.card

import android.content.Context
import com.airwallex.android.core.*
import com.airwallex.android.core.model.*

class CardComponentProvider : ActionComponentProvider<CardComponent> {

    private val cardComponent: CardComponent by lazy {
        CardComponent()
    }

    override fun canHandleAction(paymentMethodType: PaymentMethodType): Boolean {
        return paymentMethodType == PaymentMethodType.CARD
    }

    override fun retrieveSecurityToken(
        paymentIntentId: String,
        applicationContext: Context,
        securityTokenListener: SecurityTokenListener
    ) {
        AirwallexSecurityConnector().retrieveSecurityToken(
            paymentIntentId,
            applicationContext,
            securityTokenListener
        )
    }

    override fun get(): CardComponent {
        return cardComponent
    }
}
