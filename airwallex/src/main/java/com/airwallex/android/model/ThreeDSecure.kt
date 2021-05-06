package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.model.parser.ThreeDSecureParser
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThreeDSecure internal constructor(

    /**
     * Return url for 3D Secure
     */
    val returnUrl: String? = null,

    /**
     * Device data collection response for 3D Secure
     */
    val deviceDataCollectionRes: String? = null,

    /**
     * Transaction ID for 3D Secure
     */
    private var transactionId: String? = null

) : AirwallexModel, AirwallexRequestModel, Parcelable {

    override fun toParamMap(): Map<String, Any> {
        return mapOf<String, Any>()
            .plus(
                returnUrl?.let {
                    mapOf(ThreeDSecureParser.FIELD_RETURN_URL to it)
                }.orEmpty()
            )
            .plus(
                deviceDataCollectionRes?.let {
                    mapOf(ThreeDSecureParser.FIELD_COLLECTION_RES to it)
                }.orEmpty()
            )
            .plus(
                transactionId?.let {
                    mapOf(ThreeDSecureParser.FIELD_TRANSACTION_ID to it)
                }.orEmpty()
            )
    }

    class Builder : ObjectBuilder<ThreeDSecure> {
        private var returnUrl: String? = null

        private var deviceDataCollectionRes: String? = null

        private var transactionId: String? = null

        fun setReturnUrl(returnUrl: String?): Builder = apply {
            this.returnUrl = returnUrl
        }

        fun setDeviceDataCollectionRes(deviceDataCollectionRes: String?): Builder = apply {
            this.deviceDataCollectionRes = deviceDataCollectionRes
        }

        fun setTransactionId(transactionId: String?): Builder = apply {
            this.transactionId = transactionId
        }

        override fun build(): ThreeDSecure {
            return ThreeDSecure(
                returnUrl = returnUrl,
                deviceDataCollectionRes = deviceDataCollectionRes,
                transactionId = transactionId
            )
        }
    }
}
