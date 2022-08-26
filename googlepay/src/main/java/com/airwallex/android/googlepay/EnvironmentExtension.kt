package com.airwallex.android.googlepay

import com.airwallex.android.core.Environment
import com.google.android.gms.wallet.WalletConstants

fun Environment.googlePayEnvironment(): Int {
    return when (this) {
        Environment.STAGING, Environment.DEMO -> WalletConstants.ENVIRONMENT_TEST
        Environment.PRODUCTION -> WalletConstants.ENVIRONMENT_PRODUCTION
    }
}