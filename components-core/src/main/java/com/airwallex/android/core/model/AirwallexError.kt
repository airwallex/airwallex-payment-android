package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Airwallex uses conventional HTTP response codes to indicate the success or failure of an API
 * request. Typically a status code in the 2xx range indicates success, status codes in the 4xx
 * range indicate an error that has been triggered due to the information provided (for example;
 * a parameter does not meet the validation requirements or was not provided), and status codes
 * in the 5xx range indicate an error with our servers.
 */
@Parcelize
data class AirwallexError(

    /**
     * Error code
     */
    val code: String? = null,

    /**
     * Name of the request parameter that caused the error
     */
    val source: String? = null,

    /**
     * Description of the error
     */
    val message: String? = null
) : AirwallexModel, Parcelable, Serializable {

    override fun toString(): String {
        return "code $code, source $source, message $message"
    }
}
