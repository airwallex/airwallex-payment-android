package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/**
 * Data class for payment_consent options in PaymentIntentConfirmRequest as per Airwallex API
 */
@Parcelize
data class PaymentConsentOptions(
    /**
     * The party to trigger subsequent payments. One of merchant, customer (required)
     */
    val nextTriggeredBy: PaymentConsent.NextTriggeredBy,
    /**
     * Only applicable when next_triggered_by is merchant. One of scheduled, unscheduled, installments (optional)
     */
    val merchantTriggerReason: PaymentConsent.MerchantTriggerReason? = null,
    /**
     * Terms of use for this PaymentConsent (optional)
     */
    val termsOfUse: TermsOfUse? = null,
) : AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        val consentMap = mutableMapOf<String, Any>(
            "next_triggered_by" to nextTriggeredBy.value
        )
        merchantTriggerReason?.let { consentMap["merchant_trigger_reason"] = it.value }
        termsOfUse?.let { consentMap["terms_of_use"] = it.toParamMap() }
        return consentMap
    }

    /**
     * The agreed type of amounts for subsequent payment
     */
    @Parcelize
    enum class PaymentAmountType(val value: String) : Parcelable {
        FIXED("FIXED"),
        VARIABLE("VARIABLE");

        internal companion object {
            internal fun fromValue(value: String?): PaymentAmountType? {
                return values().firstOrNull { it.value == value }
            }
        }
    }
    @Parcelize
    data class TermsOfUse(
        /**
         * The agreed type of amounts for subsequent payment (required)
         */
        val paymentAmountType: PaymentAmountType,
        /**
         * The fixed payment amount that can be charged for a single payment.
         * Required if payment_amount_type is FIXED
         */
        val fixedPaymentAmount: BigDecimal? = null,
        /**
         * The maximum payment amount that can be charged for a single payment.
         * Optional if payment_amount_type is VARIABLE
         */
        val maxPaymentAmount: BigDecimal? = null,
        /**
         * The minimum payment amount that can be charged for a single payment.
         * Optional if payment_amount_type is VARIABLE
         */
        val minPaymentAmount: BigDecimal? = null,
        /**
         * The first payment amount. Optional if payment agreement type is VARIABLE
         */
        val firstPaymentAmount: BigDecimal? = null,
        /**
         * The currency of this payment
         */
        val paymentCurrency: String? = null,
        /**
         * The granularity per billing cycle.
         * Required when payment_schedule.period_unit is WEEK, MONTH, or YEAR
         */
        val billingCycleChargeDay: Int? = null,
        /**
         * Start date to expect payment request (ISO8601 format)
         */
        val startDate: String? = null,
        /**
         * End date to expect payment request (ISO8601 format)
         */
        val endDate: String? = null,
        /**
         * Total number of billing cycles
         */
        val totalBillingCycles: Int? = null,
        /**
         * Payment schedule details (optional)
         */
        val paymentSchedule: PaymentSchedule? = null
    ) : AirwallexRequestModel, Parcelable {

        override fun toParamMap(): Map<String, Any> {
            val termsMap = mutableMapOf<String, Any>(
                "payment_amount_type" to paymentAmountType.value
            )
            fixedPaymentAmount?.let { termsMap["fixed_payment_amount"] = it }
            maxPaymentAmount?.let { termsMap["max_payment_amount"] = it }
            minPaymentAmount?.let { termsMap["min_payment_amount"] = it }
            firstPaymentAmount?.let { termsMap["first_payment_amount"] = it }
            paymentCurrency?.let { termsMap["payment_currency"] = it }
            billingCycleChargeDay?.let { termsMap["billing_cycle_charge_day"] = it }
            startDate?.let { termsMap["start_date"] = it }
            endDate?.let { termsMap["end_date"] = it }
            totalBillingCycles?.let { termsMap["total_billing_cycles"] = it }
            paymentSchedule?.let { termsMap["payment_schedule"] = it.toParamMap() }
            return termsMap
        }
    }

    /**
     * Specifies billing frequency
     */
    @Parcelize
    enum class PeriodUnit(val value: String) : Parcelable {
        DAY("DAY"),
        WEEK("WEEK"),
        MONTH("MONTH"),
        YEAR("YEAR");

        internal companion object {
            internal fun fromValue(value: String?): PeriodUnit? {
                return values().firstOrNull { it.value == value }
            }
        }
    }

    @Parcelize
    data class PaymentSchedule(
        /**
         * The number of period units between billing cycles.
         * For example, period=1 and period_unit=MONTH means monthly billing.
         * Required when merchant_trigger_reason = scheduled
         */
        val period: Int? = null,
        /**
         * Specifies billing frequency.
         * Required when merchant_trigger_reason = scheduled
         */
        val periodUnit: PeriodUnit? = null,
    ) : AirwallexRequestModel, Parcelable {

        override fun toParamMap(): Map<String, Any> {
            val scheduleMap = mutableMapOf<String, Any>()
            period?.let { scheduleMap["period"] = it }
            periodUnit?.let { scheduleMap["period_unit"] = it.value }
            return scheduleMap
        }
    }
}
