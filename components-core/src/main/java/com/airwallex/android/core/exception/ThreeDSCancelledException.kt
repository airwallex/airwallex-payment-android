package com.airwallex.android.core.exception

/**
 * Exception thrown when user cancels 3D Secure authentication
 */
class ThreeDSCancelledException(
    message: String = "3DS has been cancelled!"
) : AirwallexException(null, null, 0, message)
