package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The request params to confirm [PaymentIntent]
 */
@Parcelize
data class PaymentIntentConfirmRequest internal constructor(

    /**
     * Unique request ID specified by the merchant
     */
    val requestId: String? = null,

    /**
     * Customer who intends to pay for the payment intent
     */
    val customerId: String? = null,

    /**
     * The payment method that you want to confirm
     */
    val paymentMethodRequest: PaymentMethodRequest? = null,

    /**
     * Options for payment method
     */
    val paymentMethodOptions: PaymentMethodOptions? = null,

    /**
     * Reference to an existing PaymentConsent
     */
    val paymentConsentReference: PaymentConsentReference? = null,

    /**
     * Device info
     */
    val device: Device? = null,

    /**
     * The URL to redirect your customer back to after they authenticate their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme.
     */
    val returnUrl: String? = null,

    /**
     * Integration data
     */
    val integrationData: IntegrationData? = null

) : AirwallexRequestModel, Parcelable {

    private companion object {
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_CUSTOMER_ID = "customer_id"
        private const val FIELD_PAYMENT_METHOD = "payment_method"
        private const val FIELD_PAYMENT_METHOD_OPTIONS = "payment_method_options"
        private const val FIELD_PAYMENT_CONSENT_REFERENCE = "payment_consent_reference"
        private const val FIELD_DEVICE = "device_data"
        private const val FIELD_RETURN_URL = "return_url"
        private const val FIELD_INTEGRATION_DATA = "integration_data"
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                requestId?.let {
                    mapOf(FIELD_REQUEST_ID to it)
                }.orEmpty()
            )
            .plus(
                customerId?.let {
                    mapOf(FIELD_CUSTOMER_ID to it)
                }.orEmpty()
            )
            .plus(
                paymentMethodRequest?.let {
                    mapOf(FIELD_PAYMENT_METHOD to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                paymentMethodOptions?.let {
                    mapOf(FIELD_PAYMENT_METHOD_OPTIONS to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                paymentConsentReference?.let {
                    mapOf(FIELD_PAYMENT_CONSENT_REFERENCE to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                device?.let {
                    mapOf(FIELD_DEVICE to it.toParamMap())
                }.orEmpty()
            )
            .plus(
                returnUrl?.let {
                    mapOf(FIELD_RETURN_URL to it)
                }.orEmpty()
            )
            .plus(
                mapOf(
                    FIELD_INTEGRATION_DATA to (
                        integrationData ?: IntegrationData(
                            type = sdkType,
                            version = sdkVersion
                        )
                        ).toParamMap()
                )
            )
    }

    class Builder(
        private val requestId: String
    ) : ObjectBuilder<PaymentIntentConfirmRequest> {
        private var customerId: String? = null
        private var paymentMethodRequest: PaymentMethodRequest? = null
        private var paymentMethodOptions: PaymentMethodOptions? = null
        private var paymentConsentReference: PaymentConsentReference? = null
        private var device: Device? = null
        private var returnUrl: String? = null
        private var integrationData: IntegrationData? = null

        fun setCustomerId(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun setDevice(device: Device?): Builder = apply {
            this.device = device
        }

        fun setPaymentMethodRequest(paymentMethodRequest: PaymentMethodRequest?): Builder = apply {
            this.paymentMethodRequest = paymentMethodRequest
        }

        fun setPaymentMethodOptions(paymentMethodOptions: PaymentMethodOptions?): Builder =
            apply {
                this.paymentMethodOptions = paymentMethodOptions
            }

        fun setPaymentConsentReference(paymentConsentReference: PaymentConsentReference?): Builder =
            apply {
                this.paymentConsentReference = paymentConsentReference
            }

        fun setReturnUrl(returnUrl: String?): Builder = apply {
            this.returnUrl = returnUrl
        }

        fun setIntegrationData(integrationData: IntegrationData?): Builder = apply {
            this.integrationData = integrationData
        }

        override fun build(): PaymentIntentConfirmRequest {
            return PaymentIntentConfirmRequest(
                requestId = requestId,
                customerId = customerId,
                paymentMethodRequest = paymentMethodRequest,
                paymentMethodOptions = paymentMethodOptions,
                paymentConsentReference = paymentConsentReference,
                device = device,
                returnUrl = returnUrl,
                integrationData = integrationData
            )
        }
    }
}
