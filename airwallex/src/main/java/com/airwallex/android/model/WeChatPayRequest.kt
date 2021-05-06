package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.WeChatPayRequestParser
import kotlinx.parcelize.Parcelize

/**
 * Request for WeChatPay
 */
@Parcelize
data class WeChatPayRequest constructor(

    /**
     * The specific WeChat Pay flow to use.
     */
    val flow: ThirdPartPayRequestFlow? = null

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                flow?.let {
                    mapOf(WeChatPayRequestParser.FIELD_FLOW to it.value)
                }.orEmpty()
            )
    }
}
