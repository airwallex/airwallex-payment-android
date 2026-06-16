//[components-core](../../../index.md)/[com.airwallex.android.core.util](../index.md)/[AddressSpec](index.md)/[mapState](map-state.md)

# mapState

[androidJvm]\
fun [mapState](map-state.md)(countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), stateInput: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)

Normalize a free-text state to its canonical dropdown value.

Matches case-insensitively against either the option value or label. Returns the input unchanged when no list exists or no match is found, so callers can pass arbitrary user input through.
