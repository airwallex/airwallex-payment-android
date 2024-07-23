package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.math.BigDecimal

@Parcelize
data class NextAction(

    val stage: NextActionStage? = null,
    /**
     * Type of next action, can be one of render_qr_code, call_sdk, redirect, display
     */
    val type: NextActionType?,

    /**
     * The additional data that can be used to complete this action
     */
    val data: @RawValue Map<String, Any?>?,

    /**
     * The dcc data that can be used to complete this action
     */
    val dcc: DccData?,

    val url: String?,

    val method: String?,

    val packageName: String?
) : AirwallexModel, Parcelable {

    @Parcelize
    enum class NextActionStage(val value: String) : Parcelable {

        WAITING_DEVICE_DATA_COLLECTION("WAITING_DEVICE_DATA_COLLECTION"),

        WAITING_USER_INFO_INPUT("WAITING_USER_INFO_INPUT");

        internal companion object {
            internal fun fromValue(value: String?): NextActionStage? {
                return values().firstOrNull { it.value == value }
            }
        }
    }

    /**
     * The status of a [PaymentIntent]
     */
    @Parcelize
    data class DccData internal constructor(
        val currency: String?,

        val amount: BigDecimal?,

        val currencyPair: String?,

        val clientRate: Double?,

        val rateSource: String?,

        val rateTimestamp: String?,

        val rateExpiry: String?

    ) : AirwallexModel, Parcelable

    /**
     * The status of a [PaymentIntent]
     */
    @Parcelize
    enum class NextActionType(val value: String) : Parcelable {

        RENDER_QR_CODE("render_qr_code"),

        CALL_SDK("call_sdk"),

        REDIRECT("redirect"),

        REDIRECT_FORM("redirect_form"),

        DISPLAY("display"),

        DCC("dcc");

        internal companion object {
            internal fun fromValue(value: String?): NextActionType? {
                return values().firstOrNull { it.value == value }
            }
        }
    }
}
