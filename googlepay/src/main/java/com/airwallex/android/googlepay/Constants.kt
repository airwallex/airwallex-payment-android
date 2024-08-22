package com.airwallex.android.googlepay

import com.airwallex.android.core.googlePaySupportedNetworks

object Constants {
    val DEFAULT_SUPPORTED_METHODS = listOf(
        "PAN_ONLY",
        "CRYPTOGRAM_3DS"
    )

    val DEFAULT_SUPPORTED_CARD_NETWORKS = googlePaySupportedNetworks()

    const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "airwallex"

}
