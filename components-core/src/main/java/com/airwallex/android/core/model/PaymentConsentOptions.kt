package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
     * Only applicable when next_triggered_by is merchant. One of scheduled, unscheduled (optional)
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
    @Parcelize
    data class TermsOfUse(
        /**
         * The agreed type of amounts for subsequent payment. One of FIXED, VARIABLE (required)
         */
        val paymentAmountType: String,
        /**
         * The fixed payment amount that can be charged for a single payment.
         * Required if payment_amount_type is FIXED
         */
        val fixedPaymentAmount: Double? = null,
        /**
         * The maximum payment amount that can be charged for a single payment.
         * Optional if payment_amount_type is VARIABLE
         */
        val maxPaymentAmount: Double? = null,
        /**
         * The minimum payment amount that can be charged for a single payment.
         * Optional if payment_amount_type is VARIABLE
         */
        val minPaymentAmount: Double? = null,
        /**
         * The first payment amount. Optional if payment agreement type is VARIABLE
         */
        val firstPaymentAmount: Double? = null,
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
         * End date to expect payment request (ISO8601 format)
         */
        val endDate: String? = null,
        /**
         * Payment schedule details (optional)
         */
        val paymentSchedule: PaymentSchedule? = null
    ) : AirwallexRequestModel, Parcelable {

        override fun toParamMap(): Map<String, Any> {
            val termsMap = mutableMapOf<String, Any>(
                "payment_amount_type" to paymentAmountType
            )
            fixedPaymentAmount?.let { termsMap["fixed_payment_amount"] = it }
            maxPaymentAmount?.let { termsMap["max_payment_amount"] = it }
            minPaymentAmount?.let { termsMap["min_payment_amount"] = it }
            firstPaymentAmount?.let { termsMap["first_payment_amount"] = it }
            paymentCurrency?.let { termsMap["payment_currency"] = it }
            billingCycleChargeDay?.let { termsMap["billing_cycle_charge_day"] = it }
            endDate?.let { termsMap["end_date"] = it }
            paymentSchedule?.let { termsMap["payment_schedule"] = it.toParamMap() }
            return termsMap
        }
    }

    @Parcelize
    data class PaymentSchedule(
        /**
         * Start date of the payment schedule (ISO8601 format)
         */
        val startDate: String? = null,
        /**
         * End date of the payment schedule (ISO8601 format)
         */
        val endDate: String? = null,
        /**
         * The number of period units between billing cycles.
         * For example, period=1 and period_unit=MONTH means monthly billing.
         * Required when merchant_trigger_reason = scheduled
         */
        val period: Int? = null,
        /**
         * Specifies billing frequency. One of DAY, WEEK, MONTH, and YEAR.
         * Required when merchant_trigger_reason = scheduled
         */
        val periodUnit: String? = null,
        /**
         * Total number of billing cycles
         */
        val totalBillingCycles: Int? = null
    ) : AirwallexRequestModel, Parcelable {

        override fun toParamMap(): Map<String, Any> {
            val scheduleMap = mutableMapOf<String, Any>()
            startDate?.let { scheduleMap["start_date"] = it }
            endDate?.let { scheduleMap["end_date"] = it }
            period?.let { scheduleMap["period"] = it }
            periodUnit?.let { scheduleMap["period_unit"] = it }
            totalBillingCycles?.let { scheduleMap["total_billing_cycles"] = it }
            return scheduleMap
        }
    }
}
