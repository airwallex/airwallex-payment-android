package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.AliPayRequestParser
import kotlinx.android.parcel.Parcelize

/**
 * Request for WeChatPay
 */
@Parcelize
data class AliPayRequest constructor(

    /**
     * The specific AliPay Pay flow to use.
     */
    val flow: ThirdPartPayRequestFlow? = null,

    val osType: String? = null

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                flow?.let {
                    mapOf(AliPayRequestParser.FIELD_FLOW to it.value)
                }.orEmpty()
            )
            .plus(
                mapOf(AliPayRequestParser.FIELD_OS_TYPE to "android")
            )
    }
}
