package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.*

@Parcelize
data class PaymentConsent internal constructor(

    /**
     * Unique identifier of this PaymentConsent
     */
    val id: String? = null,

    /**
     * Unique request ID specified by the merchant
     */
    val requestId: String? = null,

    /**
     * Airwallex ID of the customer for whom the PaymentConsent is created
     */
    val customerId: String? = null,

    /**
     * PaymentMethod information attached for subsequent payments
     */
    var paymentMethod: PaymentMethod? = null,

    /**
     * ID of the initial PaymentIntent confirmed with this PaymentConsent
     */
    val initialPaymentIntentId: String? = null,

    /**
     * The party to trigger subsequent payments. One of merchant, customer
     */
    val nextTriggeredBy: NextTriggeredBy? = null,

    /**
     * Only applicable when next_triggered_by is merchant. One of scheduled, unscheduled
     */
    val merchantTriggerReason: MerchantTriggerReason? = null,

    /**
     * Only applicable when next_triggered_by is customer. If false, the customer must provide cvc for subsequent payments with this PaymentConsent.
     */
    val requiresCvc: Boolean = false,

    /**
     * A set of key-value pairs that can be attached to this PaymentConsent
     */
    val metadata: Map<String, @RawValue Any?>? = null,

    /**
     * Status of this PaymentConsent. One of PENDING_VERIFICATION, VERIFIED, DISABLED
     */
    val status: PaymentConsentStatus? = null,

    /**
     * Time at which this PaymentConsent was created
     */
    val createdAt: Date? = null,

    /**
     * Time at which this PaymentConsent was last updated
     */
    val updatedAt: Date? = null,

    /**
     * Next action for merchant
     */
    val nextAction: NextAction? = null,

    /**
     * Client secret for browser or app
     */
    val clientSecret: String? = null

) : AirwallexModel, Parcelable {

    /**
     * The party to trigger subsequent payments.
     */
    @Parcelize
    enum class NextTriggeredBy(val value: String) : Parcelable {
        MERCHANT("merchant"),

        CUSTOMER("customer");

        internal companion object {
            internal fun fromValue(value: String?): NextTriggeredBy? {
                return values().firstOrNull { it.value == value }
            }
        }
    }

    /**
     * Only applicable when next_triggered_by is merchant
     */
    @Parcelize
    enum class MerchantTriggerReason(val value: String) : Parcelable {
        SCHEDULED("scheduled"),

        UNSCHEDULED("unscheduled");

        internal companion object {
            internal fun fromValue(value: String?): MerchantTriggerReason? {
                return values().firstOrNull { it.value == value }
            }
        }
    }

    /**
     * Only applicable when next_triggered_by is merchant
     */
    @Parcelize
    enum class PaymentConsentStatus(val value: String) : Parcelable {
        PENDING_VERIFICATION("PENDING_VERIFICATION"),

        VERIFIED("VERIFIED"),

        DISABLED("DISABLED");

        internal companion object {
            internal fun fromValue(value: String?): PaymentConsentStatus? {
                return values().firstOrNull { it.value == value }
            }
        }
    }
}
