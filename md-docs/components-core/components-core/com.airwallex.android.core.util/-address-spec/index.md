//[components-core](../../../index.md)/[com.airwallex.android.core.util](../index.md)/[AddressSpec](index.md)

# AddressSpec

[androidJvm]\
@[RestrictTo](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.html)(value = [[RestrictTo.Scope.LIBRARY_GROUP](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.Scope.LIBRARY_GROUP.html)])

object [AddressSpec](index.md)

Per-country address spec — what the merchant's address form should look like for each ISO country code.

Each country has an `fmt` string that names the fields it collects in render order: `%A` = street, `%C` = city, `%S` = state, `%Z` = postcode. The presence of a token decides whether that field is rendered:

- 
   State (`%S`): [hasState](has-state.md) / [stateList](state-list.md)
- 
   City (`%C`): [hasCity](has-city.md)
- 
   Postcode (`%Z`): [hasPostcode](has-postcode.md) / [postcodePattern](postcode-pattern.md) / [postcodeExamples](postcode-examples.md)

Countries with no `fmt` defined fall back to the default of street + city.

**Required-field policy.** Any field that is visible is required. The upstream data also declares a per-country `require` set (e.g. street + state for AE), but in practice the validator collects every visible field with a blank check, so the `require` declaration is intentionally not exposed here.

## Functions

| Name | Summary |
|---|---|
| [hasCity](has-city.md) | [androidJvm]<br>fun [hasCity](has-city.md)(countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Whether the city field should be rendered for the given country. |
| [hasPostcode](has-postcode.md) | [androidJvm]<br>fun [hasPostcode](has-postcode.md)(countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Whether the postcode field should be rendered for the given country. |
| [hasState](has-state.md) | [androidJvm]<br>fun [hasState](has-state.md)(countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Whether the given country shows a state field at all. |
| [mapState](map-state.md) | [androidJvm]<br>fun [mapState](map-state.md)(countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), stateInput: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Normalize a free-text state to its canonical dropdown value. |
| [postcodeExamples](postcode-examples.md) | [androidJvm]<br>fun [postcodeExamples](postcode-examples.md)(countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;?<br>Sample postcodes for the country (use the first as a hint/placeholder), or null. |
| [postcodePattern](postcode-pattern.md) | [androidJvm]<br>fun [postcodePattern](postcode-pattern.md)(countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Regex](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/-regex/index.html)?<br>Anchored, case-insensitive regex for the country's postcode, or null if none. |
| [stateList](state-list.md) | [androidJvm]<br>fun [stateList](state-list.md)(countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[Pair](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-pair/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;&gt;?<br>State dropdown options for the given country, or null when there is no fixed list. |
