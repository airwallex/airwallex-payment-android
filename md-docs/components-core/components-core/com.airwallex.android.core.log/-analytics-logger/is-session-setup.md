//[components-core](../../../index.md)/[com.airwallex.android.core.log](../index.md)/[AnalyticsLogger](index.md)/[isSessionSetup](is-session-setup.md)

# isSessionSetup

[androidJvm]\
fun [isSessionSetup](is-session-setup.md)(session: [AirwallexSession](../../com.airwallex.android.core/-airwallex-session/index.md)): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)

Check if analytics session is already set up for the given session. Returns false if session has changed, allowing re-initialization.

#### Return

true if analytics is already set up for this session

#### Parameters

androidJvm

| | |
|---|---|
| session | The session to check |
