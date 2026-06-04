//[components-core](../../index.md)/[com.airwallex.android.core](index.md)/[bindToActivity](bind-to-activity.md)

# bindToActivity

[androidJvm]\
fun [AirwallexSession](-airwallex-session/index.md).[bindToActivity](bind-to-activity.md)(activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html))

Binds this session's PaymentIntentProvider to an Activity lifecycle. This ensures the provider is cleaned up when the Activity is destroyed. Should be called once when the session starts being used with a specific Activity.

This function works with any session type that supports PaymentIntentProvider (AirwallexPaymentSession and AirwallexRecurringWithIntentSession).

#### Parameters

androidJvm

| | |
|---|---|
| activity | The host Activity that will own this session's provider |
