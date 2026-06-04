//[components-core](../../../index.md)/[com.airwallex.android.core.util](../index.md)/[CardUtils](index.md)

# CardUtils

[androidJvm]\
object [CardUtils](index.md)

## Properties

| Name | Summary |
|---|---|
| [maxCardNumberLength](max-card-number-length.md) | [androidJvm]<br>val [maxCardNumberLength](max-card-number-length.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)<br>The maximum length of a possible card number |

## Functions

| Name | Summary |
|---|---|
| [formatCardNumber](format-card-number.md) | [androidJvm]<br>fun [formatCardNumber](format-card-number.md)(input: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), brand: [CardBrand](../../com.airwallex.android.core/-card-brand/index.md)): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [getPossibleCardBrand](get-possible-card-brand.md) | [androidJvm]<br>fun [getPossibleCardBrand](get-possible-card-brand.md)(cardNumber: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, shouldNormalize: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [CardBrand](../../com.airwallex.android.core/-card-brand/index.md)<br>Get all possible card brands of the card number |
| [getSpacePositions](get-space-positions.md) | [androidJvm]<br>fun [getSpacePositions](get-space-positions.md)(cardBrand: [CardBrand](../../com.airwallex.android.core/-card-brand/index.md)): [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)&gt;<br>Get space position by spacing pattern of the card brand e.g. spacing pattern 4-4-4-4 is correlated to space position 4-9-14 |
| [isValidCardLength](is-valid-card-length.md) | [androidJvm]<br>fun [isValidCardLength](is-valid-card-length.md)(cardNumber: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, shouldNormalize: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Check if card length is valid |
| [isValidCardNumber](is-valid-card-number.md) | [androidJvm]<br>fun [isValidCardNumber](is-valid-card-number.md)(cardNumber: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Check if card number is valid |
| [isValidLuhnNumber](is-valid-luhn-number.md) | [androidJvm]<br>fun [isValidLuhnNumber](is-valid-luhn-number.md)(number: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Check if number is a valid luhn number |
| [removeSpacesAndHyphens](remove-spaces-and-hyphens.md) | [androidJvm]<br>fun [removeSpacesAndHyphens](remove-spaces-and-hyphens.md)(cardNumberWithSpaces: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>Remove all spaces and hyphens of the card number |
