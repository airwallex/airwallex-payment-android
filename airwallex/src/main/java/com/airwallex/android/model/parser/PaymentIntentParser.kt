package com.airwallex.android.model.parser

import com.airwallex.android.model.*
import com.airwallex.android.model.AirwallexJsonUtils
import org.json.JSONObject
import java.math.BigDecimal

class PaymentIntentParser : ModelJsonParser<PaymentIntent> {

    private val paymentMethodParser: PaymentMethodParser = PaymentMethodParser()

    override fun parse(json: JSONObject): PaymentIntent {
        val availablePaymentMethodTypes = json.optJSONArray(FIELD_AVAILABLE_PAYMENT_METHOD_TYPES)?.let {
            (0 until it.length())
                .map { idx -> it.optString(idx) }
                .mapNotNull { jsonObject ->
                    AvaliablePaymentMethodType.fromValue(jsonObject)
                }
        }

        val customerPaymentMethods = json.optJSONArray(FIELD_AVAILABLE_PAYMENT_METHODS)?.let {
            (0 until it.length())
                .map { idx -> it.optJSONObject(idx) }
                .mapNotNull { jsonObject ->
                    paymentMethodParser.parse(jsonObject)
                }
        }

        return PaymentIntent(
            id = json.optString(FIELD_ID),
            requestId = AirwallexJsonUtils.optString(json, FIELD_REQUEST_ID),
            amount = BigDecimal.valueOf(json.optDouble(FIELD_AMOUNT)),
            currency = json.optString(FIELD_CURRENCY),
            merchantOrderId = AirwallexJsonUtils.optString(json, FIELD_MERCHANT_ORDER_ID),
            order = requireNotNull(
                PurchaseOrderParser().parse(requireNotNull(json.optJSONObject(FIELD_ORDER)))
            ),
            customerId = AirwallexJsonUtils.optString(json, FIELD_CUSTOM_ID),
            descriptor = AirwallexJsonUtils.optString(json, FIELD_DESCRIPTOR),
            metadata = AirwallexJsonUtils.optMap(json, FIELD_METADATA),
            status = PaymentIntentStatus.fromValue(AirwallexJsonUtils.optString(json, FIELD_STATUS)),
            capturedAmount = AirwallexJsonUtils.optDouble(json, FIELD_CAPTURED_AMOUNT)?.let {
                BigDecimal.valueOf(it)
            },
            latestPaymentAttempt = json.optJSONObject(FIELD_LAST_PAYMENT_ATTEMPT)?.let {
                PaymentAttemptParser().parse(it)
            },
            availablePaymentMethodTypes = availablePaymentMethodTypes,
            customerPaymentMethods = customerPaymentMethods,
            clientSecret = AirwallexJsonUtils.optString(json, FIELD_CLIENT_SECRET),
            nextAction = json.optJSONObject(FIELD_NEXT_ACTION)?.let {
                NextActionParser().parse(it)
            },
            createdAt = AirwallexJsonUtils.optString(json, FIELD_CREATED_AT)?.let {
                dateFormat.parse(it)
            },
            updatedAt = AirwallexJsonUtils.optString(json, FIELD_UPDATED_AT)?.let {
                dateFormat.parse(it)
            },
            cancelledAt = AirwallexJsonUtils.optString(json, FIELD_CANCELLED_AT)?.let {
                dateFormat.parse(it)
            },
            cancellationReason = AirwallexJsonUtils.optString(json, FIELD_CANCELLATION_REASON)
        )
    }

    private companion object {
        private const val FIELD_ID = "id"
        private const val FIELD_REQUEST_ID = "request_id"
        private const val FIELD_AMOUNT = "amount"
        private const val FIELD_CURRENCY = "currency"
        private const val FIELD_MERCHANT_ORDER_ID = "merchant_order_id"
        private const val FIELD_ORDER = "order"
        private const val FIELD_CUSTOM_ID = "customer_id"
        private const val FIELD_DESCRIPTOR = "descriptor"
        private const val FIELD_METADATA = "metadata"
        private const val FIELD_STATUS = "status"
        private const val FIELD_CAPTURED_AMOUNT = "captured_amount"
        private const val FIELD_LAST_PAYMENT_ATTEMPT = "latest_payment_attempt"
        private const val FIELD_AVAILABLE_PAYMENT_METHOD_TYPES = "available_payment_method_types"
        private const val FIELD_AVAILABLE_PAYMENT_METHODS = "customer_payment_methods"
        private const val FIELD_CLIENT_SECRET = "client_secret"
        private const val FIELD_NEXT_ACTION = "next_action"
        private const val FIELD_CREATED_AT = "created_at"
        private const val FIELD_UPDATED_AT = "updated_at"
        private const val FIELD_CANCELLED_AT = "cancelled_at"
        private const val FIELD_CANCELLATION_REASON = "cancellation_reason"
    }

    internal class PaymentAttemptParser : ModelJsonParser<PaymentIntent.PaymentAttempt> {

        override fun parse(json: JSONObject): PaymentIntent.PaymentAttempt? {
            return PaymentIntent.PaymentAttempt(
                id = AirwallexJsonUtils.optString(json, FIELD_ID),
                amount = AirwallexJsonUtils.optDouble(json, FIELD_AMOUNT)?.let {
                    BigDecimal.valueOf(it)
                },
                currency = AirwallexJsonUtils.optString(json, FIELD_CURRENCY),
                paymentMethod = requireNotNull(
                    PaymentMethodParser().parse(
                        requireNotNull(
                            json.optJSONObject(FIELD_PAYMENT_METHOD)
                        )
                    )
                ),
                capturedAmount = AirwallexJsonUtils.optDouble(json, FIELD_CAPTURED_AMOUNT)?.let {
                    BigDecimal.valueOf(it)
                },
                refundedAmount = AirwallexJsonUtils.optDouble(json, FIELD_REFUNDED_AMOUNT)?.let {
                    BigDecimal.valueOf(it)
                },
                createdAt = AirwallexJsonUtils.optString(json, FIELD_CREATED_AT)?.let {
                    dateFormat.parse(it)
                },
                updatedAt = AirwallexJsonUtils.optString(json, FIELD_UPDATED_AT)?.let {
                    dateFormat.parse(it)
                },
                authenticationData = json.optJSONObject(FIELD_AUTHENTICATION_DATA)?.let {
                    PaymentAttemptAuthDataParser().parse(it)
                }
            )
        }

        private companion object {
            private const val FIELD_ID = "id"
            private const val FIELD_AMOUNT = "amount"
            private const val FIELD_CURRENCY = "currency"
            private const val FIELD_PAYMENT_METHOD = "payment_method"
            private const val FIELD_CAPTURED_AMOUNT = "captured_amount"
            private const val FIELD_REFUNDED_AMOUNT = "refunded_amount"
            private const val FIELD_CREATED_AT = "created_at"
            private const val FIELD_UPDATED_AT = "updated_at"
            private const val FIELD_AUTHENTICATION_DATA = "authentication_data"
        }
    }

    internal class PaymentAttemptAuthDataParser : ModelJsonParser<PaymentIntent.PaymentAttemptAuthData> {

        override fun parse(json: JSONObject): PaymentIntent.PaymentAttemptAuthData? {
            return PaymentIntent.PaymentAttemptAuthData(
                dsData = json.optJSONObject(FIELD_DS_DATA)?.let {
                    PaymentAttemptAuthDSDataParser().parse(it)
                },
                fraudData = json.optJSONObject(FIELD_FRAUD_DATA)?.let {
                    PaymentAttemptAuthFraudDataParser().parse(it)
                },
                avsResult = AirwallexJsonUtils.optString(json, FIELD_AVS_RESULT),
                cvcResult = AirwallexJsonUtils.optString(json, FIELD_CVC_RESULT)
            )
        }

        private companion object {
            private const val FIELD_DS_DATA = "ds_data"
            private const val FIELD_FRAUD_DATA = "fraud_data"
            private const val FIELD_AVS_RESULT = "avs_result"
            private const val FIELD_CVC_RESULT = "cvc_result"
        }
    }

    internal class PaymentAttemptAuthDSDataParser : ModelJsonParser<PaymentIntent.PaymentAttemptAuthDSData> {

        override fun parse(json: JSONObject): PaymentIntent.PaymentAttemptAuthDSData? {
            return PaymentIntent.PaymentAttemptAuthDSData(
                version = AirwallexJsonUtils.optString(json, FIELD_VERSION),
                liabilityShiftIndicator = AirwallexJsonUtils.optString(json, FIELD_LIABILITY_SHIFT_INDICATOR),
                eci = AirwallexJsonUtils.optString(json, FIELD_ECI),
                cavv = AirwallexJsonUtils.optString(json, FIELD_CAVV),
                xid = AirwallexJsonUtils.optString(json, FIELD_XID),
                enrolled = AirwallexJsonUtils.optString(json, FIELD_ENROLLED),
                paResStatus = AirwallexJsonUtils.optString(json, FIELD_PA_RES_STATUS),
                challengeCancellationReason = AirwallexJsonUtils.optString(json, FIELD_CHALLENGE_CANCELLATION_REASON),
                frictionless = AirwallexJsonUtils.optString(json, FIELD_FRICTIONLESS)
            )
        }

        private companion object {
            private const val FIELD_VERSION = "version"
            private const val FIELD_LIABILITY_SHIFT_INDICATOR = "liability_shift_indicator"
            private const val FIELD_ECI = "eci"
            private const val FIELD_CAVV = "cavv"
            private const val FIELD_XID = "xid"
            private const val FIELD_ENROLLED = "enrolled"
            private const val FIELD_PA_RES_STATUS = "pa_res_status"
            private const val FIELD_CHALLENGE_CANCELLATION_REASON = "challenge_cancellation_reason"
            private const val FIELD_FRICTIONLESS = "frictionless"
        }
    }

    internal class PaymentAttemptAuthFraudDataParser : ModelJsonParser<PaymentIntent.PaymentAttemptAuthFraudData> {

        override fun parse(json: JSONObject): PaymentIntent.PaymentAttemptAuthFraudData? {
            return PaymentIntent.PaymentAttemptAuthFraudData(
                action = AirwallexJsonUtils.optString(json, FIELD_ACTION),
                score = AirwallexJsonUtils.optString(json, FIELD_SCORE)
            )
        }

        private companion object {
            private const val FIELD_ACTION = "action"
            private const val FIELD_SCORE = "score"
        }
    }

    internal class NextActionParser : ModelJsonParser<PaymentIntent.NextAction> {

        override fun parse(json: JSONObject): PaymentIntent.NextAction? {
            return PaymentIntent.NextAction(
                type = PaymentIntent.NextActionType.fromValue(
                    AirwallexJsonUtils.optString(json, FIELD_TYPE)
                ),
                data = AirwallexJsonUtils.optMap(json, FIELD_DATA),
                dcc = json.optJSONObject(FIELD_DCC_DATA)?.let {
                    DccDataParser().parse(it)
                },
                url = AirwallexJsonUtils.optString(json, FIELD_URL)
            )
        }

        private companion object {
            private const val FIELD_TYPE = "type"
            private const val FIELD_DATA = "data"
            private const val FIELD_DCC_DATA = "dcc_data"
            private const val FIELD_URL = "url"
        }
    }

    internal class DccDataParser : ModelJsonParser<PaymentIntent.DccData> {

        override fun parse(json: JSONObject): PaymentIntent.DccData? {
            return PaymentIntent.DccData(
                currency = AirwallexJsonUtils.optString(json, FIELD_CURRENCY),
                amount = AirwallexJsonUtils.optDouble(json, FIELD_AMOUNT)?.let {
                    BigDecimal.valueOf(it)
                },
                currencyPair = AirwallexJsonUtils.optString(json, FIELD_CURRENCY_PAIR),
                clientRate = AirwallexJsonUtils.optDouble(json, FIELD_CLIENT_RATE),
                rateSource = AirwallexJsonUtils.optString(json, FIELD_RATE_SOURCE),
                rateTimestamp = AirwallexJsonUtils.optString(json, FIELD_RATE_TIMESTAMP),
                rateExpiry = AirwallexJsonUtils.optString(json, FIELD_RATE_EXPIRY)
            )
        }

        private companion object {
            private const val FIELD_CURRENCY = "currency"
            private const val FIELD_AMOUNT = "amount"
            private const val FIELD_CURRENCY_PAIR = "currency_pair"
            private const val FIELD_CLIENT_RATE = "client_rate"
            private const val FIELD_RATE_SOURCE = "rate_source"
            private const val FIELD_RATE_TIMESTAMP = "rate_timestamp"
            private const val FIELD_RATE_EXPIRY = "rate_expiry"
        }
    }
}
