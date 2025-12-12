package com.airwallex.paymentacceptance.repo

import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.paymentacceptance.Settings

sealed class ReturnUrl(private val subpath: String) {
    data object UI : ReturnUrl("/ui")
    data object API : ReturnUrl("/api")

    val fullUrl: String get() = Settings.returnUrl + subpath
}

interface BaseRepository {

    suspend fun getPaymentIntentFromServer(
        force3DS: Boolean? = false,
        customerId: String? = null,
        returnUrl: ReturnUrl
    ): PaymentIntent

    suspend fun getCustomerIdFromServer(saveCustomerIdToSetting: Boolean): String

    suspend fun getClientSecretFromServer(customerId: String): String
}