package com.airwallex.paymentacceptance.repo

import com.airwallex.android.core.model.PaymentIntent

interface BaseRepository {

    suspend fun getPaymentIntentFromServer(
        force3DS: Boolean? = false,
        customerId: String? = null
    ): PaymentIntent

    suspend fun getCustomerIdFromServer(): String

    suspend fun getClientSecretFromServer(customerId: String): String
}