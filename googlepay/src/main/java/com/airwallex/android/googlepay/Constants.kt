package com.airwallex.android.googlepay

object Constants {
    val DEFAULT_SUPPORTED_METHODS = listOf(
        "PAN_ONLY",
        "CRYPTOGRAM_3DS"
    )

    val DEFAULT_SUPPORTED_CARD_NETWORKS = com.airwallex.android.core.DEFAULT_SUPPORTED_CARD_NETWORKS

    const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "airwallex"

}
