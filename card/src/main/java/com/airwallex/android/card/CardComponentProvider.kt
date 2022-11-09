package com.airwallex.android.card

import android.app.Activity
import com.airwallex.android.core.*
import com.airwallex.android.core.model.*

class CardComponentProvider : ActionComponentProvider<CardComponent> {

    private val cardComponent: CardComponent by lazy {
        CardComponent()
    }

    override fun canHandleAction(nextAction: NextAction?): Boolean {
        return nextAction == null ||
            nextAction.type == NextAction.NextActionType.DCC ||
            nextAction.type == NextAction.NextActionType.REDIRECT && nextAction.data != null ||
            nextAction.type == NextAction.NextActionType.REDIRECT_FORM && nextAction.data != null
    }

    override fun get(): CardComponent {
        return cardComponent
    }

    override fun getType(): ActionComponentProviderType {
        return ActionComponentProviderType.CARD
    }

    override suspend fun canHandleSessionAndPaymentMethod(
        session: AirwallexSession,
        paymentMethodType: AvailablePaymentMethodType,
        activity: Activity
    ): Boolean {
        return !paymentMethodType.cardSchemes.isNullOrEmpty()
    }
}
