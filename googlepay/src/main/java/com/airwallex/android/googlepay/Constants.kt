package com.airwallex.android.googlepay

import com.google.android.gms.wallet.WalletConstants

object Constants {
    val DEFAULT_SUPPORTED_METHODS = listOf(
        "PAN_ONLY",
        "CRYPTOGRAM_3DS"
    )

    val DEFAULT_SUPPORTED_CARD_NETWORKS = listOf(
        "MASTERCARD",
        "VISA"
    )

    const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "airwallex"

    const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST
}
