package com.airwallex.android

import com.airwallex.android.model.ThreeDSecureLookup

internal interface ThreeDSecureLookupListener {

    fun onLookupComplete(
        threeDSecureLookup: ThreeDSecureLookup
    )
}
