package com.airwallex.android.redirect

import com.airwallex.android.core.ActionComponent
import com.airwallex.android.core.ActionComponentProvider

class RedirectComponent : ActionComponent {

    companion object {

        val PROVIDER: ActionComponentProvider<RedirectComponent> = RedirectComponentProvider()
    }
}
