package com.airwallex.android

import com.airwallex.android.model.ThreeDSecureLookup

interface ThreeDSecureLookupListener {

    fun onLookupComplete(
        threeDSecureLookup: ThreeDSecureLookup
    )
}