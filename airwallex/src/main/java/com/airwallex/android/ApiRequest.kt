package com.airwallex.android

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

internal class ApiRequest internal constructor(
    method: AirwallexRequest.Method,
    url: String,
    params: Map<String, *>? = null,
    internal val options: Options
) : AirwallexRequest(method, url, params) {

    @Parcelize
    internal data class Options internal constructor(
        val apiKey: String,
        internal val clientId: String? = null,
        val url: String
    ) : Parcelable {

        init {
            ApiKeyValidator.requireValid(apiKey)
        }

        internal companion object {

            @JvmSynthetic
            internal fun createGet(
                url: String,
                options: Options,
                params: Map<String, *>? = null
            ): ApiRequest {
                return ApiRequest(Method.GET, url, params, options)
            }

            @JvmSynthetic
            internal fun createPost(
                url: String,
                options: Options,
                params: Map<String, *>? = null
            ): ApiRequest {
                return ApiRequest(Method.POST, url, params, options)
            }

            @JvmSynthetic
            internal fun createDelete(
                url: String,
                options: Options
            ): ApiRequest {
                return ApiRequest(Method.DELETE, url, null, options)
            }
        }

    }

    override fun getOutputBytes(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}