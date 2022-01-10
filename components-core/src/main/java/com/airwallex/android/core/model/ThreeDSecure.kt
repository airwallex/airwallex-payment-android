package com.airwallex.android.core.model

import android.os.Parcelable
import com.airwallex.android.core.model.parser.ThreeDSecureParser
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThreeDSecure internal constructor(

    /**
     * Return url for 3D Secure
     */
    val returnUrl: String? = null,

    /**
     * ACS response 3D Secure
     */
    val acsResponse: String? = null
) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                returnUrl?.let {
                    mapOf(ThreeDSecureParser.FIELD_RETURN_URL to it)
                }.orEmpty()
            )
            .plus(
                acsResponse?.let {
                    mapOf(ThreeDSecureParser.FIELD_ACS_RESPONSE to it)
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<ThreeDSecure> {
        private var returnUrl: String? = null

        private var acsResponse: String? = null

        fun setReturnUrl(returnUrl: String?): Builder = apply {
            this.returnUrl = returnUrl
        }

        fun setAcsResponse(acsResponse: String?): Builder = apply {
            this.acsResponse = acsResponse
        }

        override fun build(): ThreeDSecure {
            return ThreeDSecure(
                returnUrl = returnUrl,
                acsResponse = acsResponse
            )
        }
    }
}
