package com.airwallex.android.core.log

interface TrackablePage {
    val pageName: String
    val additionalInfo: Map<String, Any>
        get() = emptyMap()
}