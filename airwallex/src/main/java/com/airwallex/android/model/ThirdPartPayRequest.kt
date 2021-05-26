package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.ThirdPartPayRequestParser
import kotlinx.parcelize.Parcelize

/**
 * Request for Third Part Pay
 */
@Parcelize
data class ThirdPartPayRequest constructor(

    /**
     * The specific AliPay Pay flow to use.
     */
    val flow: ThirdPartPayRequestFlow? = null,

    val osType: String? = null

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                mapOf(ThirdPartPayRequestParser.FIELD_FLOW to ThirdPartPayRequestFlow.IN_APP.value)
            )
            .plus(
                mapOf(ThirdPartPayRequestParser.FIELD_OS_TYPE to "android")
            )
    }
}
