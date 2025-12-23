package com.airwallex.paymentacceptance.repo

import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.paymentacceptance.Settings

enum class DemoReturnUrl(private val subpath: String) {
    UIIntegration("/ui"),
    APIIntegration("/api");

    val fullUrl: String get() = Settings.returnUrl + subpath
}

interface BaseRepository {

    suspend fun getPaymentIntentFromServer(
        force3DS: Boolean? = false,
        customerId: String? = null,
        returnUrl: DemoReturnUrl
    ): PaymentIntent

    suspend fun getCustomerIdFromServer(saveCustomerIdToSetting: Boolean): String

    suspend fun getClientSecretFromServer(customerId: String): String
}