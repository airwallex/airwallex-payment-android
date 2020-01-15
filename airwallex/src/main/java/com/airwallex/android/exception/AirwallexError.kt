package com.airwallex.android.exception

import java.io.Serializable

data class AirwallexError internal constructor(

    val type: String? = null,

    val message: String? = null,

    val code: String? = null,

    val param: String? = null,

    val declineCode: String? = null,

    val charge: String? = null
) : Serializable
