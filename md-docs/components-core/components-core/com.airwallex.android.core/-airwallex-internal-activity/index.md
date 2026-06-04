//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexInternalActivity](index.md)

# AirwallexInternalActivity

[androidJvm]\
interface [AirwallexInternalActivity](index.md)

Marker interface for Airwallex SDK internal activities. Used to distinguish between SDK UI integration (activities extending this) and API integration (merchant's own activities calling Airwallex APIs directly).

When Airwallex methods are called from an activity implementing this interface, certain events (like payment_launched) are not logged to avoid duplicate tracking.
