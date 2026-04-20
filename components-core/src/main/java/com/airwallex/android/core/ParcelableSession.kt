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
class ParcelableSession internal constructor(
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
    val requiresCVC: Boolean,
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
            requiresCVC = requiresCVC,
            hidePaymentConsents = hidePaymentConsents
        ).also {
            it.paymentIntentProvider = provider
        }
    }

    companion object {
        fun from(session: Session): ParcelableSession {
            val providerId = session.paymentIntentProviderId
                ?: session.paymentIntentProvider?.let { provider ->
                    PaymentIntentProviderRepository.store(provider)
                        .also { id -> session.paymentIntentProviderId = id }
                }

            return ParcelableSession(
                paymentIntent = session.paymentIntent,
                paymentIntentProviderId = providerId,
                paymentConsentOptions = session.paymentConsentOptions,
                currency = session.currency,
                countryCode = session.countryCode,
                amount = session.amount,
                shipping = session.shipping,
                isBillingInformationRequired = session.isBillingInformationRequired,
                isEmailRequired = session.isEmailRequired,
                customerId = session.customerId,
                returnUrl = session.returnUrl,
                googlePayOptions = session.googlePayOptions,
                paymentMethods = session.paymentMethods,
                autoCapture = session.autoCapture,
                requiresCVC = session.requiresCVC,
                hidePaymentConsents = session.hidePaymentConsents
            )
        }
    }
}
