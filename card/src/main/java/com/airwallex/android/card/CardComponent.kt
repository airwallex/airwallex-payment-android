package com.airwallex.android.card

import com.airwallex.android.core.ActionComponent
import com.airwallex.android.core.ActionComponentProvider

class CardComponent : ActionComponent {

    companion object {

        val PROVIDER: ActionComponentProvider<CardComponent> = CardComponentProvider()
    }
}
