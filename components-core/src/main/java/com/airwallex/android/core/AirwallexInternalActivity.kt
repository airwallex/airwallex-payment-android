package com.airwallex.android.core

/**
 * Marker interface for Airwallex SDK internal activities.
 * Used to distinguish between SDK UI integration (activities extending this) and
 * API integration (merchant's own activities calling Airwallex APIs directly).
 *
 * When Airwallex methods are called from an activity implementing this interface,
 * certain events (like payment_launched) are not logged to avoid duplicate tracking.
 */
interface AirwallexInternalActivity
