//[components-core](../../index.md)/[com.airwallex.android.core.util](index.md)/[isValidE164Phone](is-valid-e164-phone.md)

# isValidE164Phone

[androidJvm]\
fun [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html).[isValidE164Phone](is-valid-e164-phone.md)(): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)

E.164: leading `+` or start immediately, country digit 1-9, then 1-14 more digits (15 max total). Whitespace and formatting characters are NOT allowed — callers should strip them first if needed.
