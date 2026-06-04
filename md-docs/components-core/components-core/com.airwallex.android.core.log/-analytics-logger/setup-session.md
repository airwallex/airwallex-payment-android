//[components-core](../../../index.md)/[com.airwallex.android.core.log](../index.md)/[AnalyticsLogger](index.md)/[setupSession](setup-session.md)

# setupSession

[androidJvm]\
fun [setupSession](setup-session.md)(session: [AirwallexSession](../../com.airwallex.android.core/-airwallex-session/index.md), launchType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), layout: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, showsGooglePayAsPrimaryButton: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null)

Helper function to set up analytics session information from an AirwallexSession.

#### Parameters

androidJvm

| | |
|---|---|
| session | The Airwallex session containing transaction details |
| launchType | The launch type identifier (e.g., &quot;dropin&quot;, &quot;component&quot;, &quot;embedded_element&quot;, &quot;api&quot;) |
| layout | The layout type as string: &quot;tab&quot;, &quot;accordion&quot;, &quot;none&quot;, or null |
