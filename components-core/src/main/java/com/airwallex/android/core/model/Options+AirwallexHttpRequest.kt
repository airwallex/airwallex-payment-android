package com.airwallex.android.core.model

import android.net.Uri
import com.airwallex.android.core.AirwallexApiRepository.Companion.confirmPaymentIntentUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.continuePaymentIntentUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.createPaymentConsentUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.createPaymentMethodUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.disablePaymentConsentUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.retrieveAvailablePaymentConsentsUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.retrieveAvailablePaymentMethodsUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.retrieveBanksUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.retrievePaymentIntentUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.retrievePaymentMethodTypeInfoUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.trackerUrl
import com.airwallex.android.core.AirwallexApiRepository.Companion.verifyPaymentConsentUrl
import com.airwallex.android.core.AirwallexPlugins
import com.airwallex.android.core.http.AirwallexHttpRequest
import java.util.*

@Suppress("LongMethod")
fun Options.toAirwallexHttpRequest(): AirwallexHttpRequest {
    val url = getUrl()

    return when (this) {
        is Options.ConfirmPaymentIntentOptions -> AirwallexHttpRequest.createPost(
            url = url,
            options = this,
            params = request.toParamMap()
        )
        is Options.ContinuePaymentIntentOptions -> AirwallexHttpRequest.createPost(
            url = url,
            options = this,
            params = request.toParamMap()
        )
        is Options.RetrievePaymentIntentOptions -> AirwallexHttpRequest.createGet(
            url = url,
            options = this,
            params = null
        )
        is Options.CreatePaymentMethodOptions -> AirwallexHttpRequest.createPost(
            url = url,
            options = this,
            params = request.toParamMap()
        )
        is Options.CreatePaymentConsentOptions -> AirwallexHttpRequest.createPost(
            url = url,
            options = this,
            params = request.toParamMap()
        )
        is Options.VerifyPaymentConsentOptions -> AirwallexHttpRequest.createPost(
            url = url,
            options = this,
            params = request.toParamMap()
        )
        is Options.DisablePaymentConsentOptions -> AirwallexHttpRequest.createPost(
            url = url,
            options = this,
            params = request.toParamMap()
        )
        is Options.RetrieveAvailablePaymentMethodsOptions -> AirwallexHttpRequest.createGet(
            url = url,
            options = this,
            params = null
        )
        is Options.RetrieveBankOptions -> AirwallexHttpRequest.createGet(
            url = url,
            options = this,
            params = null
        )
        is Options.RetrievePaymentConsentOptions -> AirwallexHttpRequest.createGet(
            url = url,
            options = this,
            params = null,
            awxTracker = UUID.randomUUID().toString()
        )
        is Options.RetrieveAvailablePaymentConsentsOptions -> AirwallexHttpRequest.createGet(
            url = url,
            options = this,
            params = null
        )
        is Options.RetrievePaymentMethodTypeInfoOptions -> AirwallexHttpRequest.createGet(
            url = url,
            options = this,
            params = null
        )
        is Options.TrackerOptions -> AirwallexHttpRequest.createGet(
            url = url,
            options = this,
            params = null
        )
    }
}

@Suppress("LongMethod")
fun Options.getUrl(): String {
    return when (this) {
        is Options.ContinuePaymentIntentOptions -> continuePaymentIntentUrl(
            AirwallexPlugins.environment.baseUrl(),
            paymentIntentId
        )
        is Options.ConfirmPaymentIntentOptions -> confirmPaymentIntentUrl(
            AirwallexPlugins.environment.baseUrl(),
            paymentIntentId
        )
        is Options.RetrievePaymentIntentOptions -> retrievePaymentIntentUrl(
            AirwallexPlugins.environment.baseUrl(),
            paymentIntentId
        )
        is Options.CreatePaymentMethodOptions -> createPaymentMethodUrl(
            AirwallexPlugins.environment.baseUrl()
        )
        is Options.CreatePaymentConsentOptions -> createPaymentConsentUrl(
            AirwallexPlugins.environment.baseUrl()
        )
        is Options.VerifyPaymentConsentOptions -> verifyPaymentConsentUrl(
            AirwallexPlugins.environment.baseUrl(),
            paymentConsentId
        )
        is Options.DisablePaymentConsentOptions -> disablePaymentConsentUrl(
            AirwallexPlugins.environment.baseUrl(),
            paymentConsentId
        )
        is Options.RetrievePaymentConsentOptions -> retrievePaymentIntentUrl(
            AirwallexPlugins.environment.baseUrl(),
            paymentConsentId
        )
        is Options.RetrieveAvailablePaymentConsentsOptions -> retrieveAvailablePaymentConsentsUrl(
            AirwallexPlugins.environment.baseUrl(),
            merchantTriggerReason,
            nextTriggerBy,
            pageNum,
            pageSize
        )
        is Options.TrackerOptions -> createTrackerUrl()
        is Options.RetrieveAvailablePaymentMethodsOptions -> retrieveAvailablePaymentMethodsUrl(
            AirwallexPlugins.environment.baseUrl(),
            pageNum,
            pageSize,
            active,
            transactionCurrency,
            transactionMode,
            countryCode
        )
        is Options.RetrievePaymentMethodTypeInfoOptions -> retrievePaymentMethodTypeInfoUrl(
            AirwallexPlugins.environment.baseUrl(),
            paymentMethodType,
            countryCode,
            flow,
            openId
        )
        is Options.RetrieveBankOptions -> retrieveBanksUrl(
            AirwallexPlugins.environment.baseUrl(),
            paymentMethodType,
            countryCode,
            flow,
            openId
        )
    }
}

private fun Options.TrackerOptions.createTrackerUrl(): String {
    val params = request.toParamMap()
    val builder = Uri.parse(trackerUrl()).buildUpon()
    params.forEach {
        builder.appendQueryParameter(it.key, it.value.toString())
    }
    val uri = builder.build()
    return uri.toString()
}