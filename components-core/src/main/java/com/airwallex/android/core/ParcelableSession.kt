package com.airwallex.android.core

import android.os.Parcelable
import androidx.annotation.RestrictTo
import com.airwallex.android.core.model.PaymentConsentOptions
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.Shipping
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Suppress("LongParameterList")
@Parcelize
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class ParcelableSession(
    val paymentIntent: PaymentIntent?,
    val paymentIntentProviderId: String?,
    val paymentConsentOptions: PaymentConsentOptions?,
    val currency: String,
    val countryCode: String,
    val amount: BigDecimal,
    val shipping: Shipping?,
    val isBillingInformationRequired: Boolean,
    val isEmailRequired: Boolean,
    val customerId: String?,
    val returnUrl: String?,
    val googlePayOptions: GooglePayOptions?,
    val paymentMethods: List<String>?,
    val autoCapture: Boolean,
    val hidePaymentConsents: Boolean
) : Parcelable {

    fun toSession(): Session {
        val providerId = paymentIntentProviderId
        val provider = providerId?.let { PaymentIntentProviderRepository.get(it) }

        return Session(
            paymentIntent = paymentIntent,
            paymentIntentProviderId = providerId,
            paymentConsentOptions = paymentConsentOptions,
            currency = currency,
            countryCode = countryCode,
            amount = amount,
            shipping = shipping,
            isBillingInformationRequired = isBillingInformationRequired,
            isEmailRequired = isEmailRequired,
            customerId = customerId,
            returnUrl = returnUrl,
            googlePayOptions = googlePayOptions,
            paymentMethods = paymentMethods,
            autoCapture = autoCapture,
            hidePaymentConsents = hidePaymentConsents
        ).also {
            it.paymentIntentProvider = provider
        }
    }

}
