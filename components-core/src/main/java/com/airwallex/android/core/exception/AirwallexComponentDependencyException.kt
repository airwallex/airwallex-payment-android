package com.airwallex.android.core.exception

import com.airwallex.android.core.model.Dependency

/**
 * An exception that is thrown when a required dependency for a component within the Airwallex SDK is missing.
 */
class AirwallexComponentDependencyException(
    dependency: Dependency,
    traceId: String? = null,
    statusCode: Int = 0,
    e: Throwable? = null
) : AirwallexException(null, traceId, statusCode, "Missing ${dependency.value} dependency!", e)
