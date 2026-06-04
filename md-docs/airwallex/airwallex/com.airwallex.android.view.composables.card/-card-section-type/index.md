//[airwallex](../../../index.md)/[com.airwallex.android.view.composables.card](../index.md)/[CardSectionType](index.md)

# CardSectionType

sealed interface [CardSectionType](index.md)

#### Inheritors

| |
|---|
| [AddCard](-add-card/index.md) |
| [ConsentList](-consent-list/index.md) |
| [ConsentDetail](-consent-detail/index.md) |

## Types

| Name | Summary |
|---|---|
| [AddCard](-add-card/index.md) | [androidJvm]<br>object [AddCard](-add-card/index.md) : [CardSectionType](index.md) |
| [ConsentDetail](-consent-detail/index.md) | [androidJvm]<br>data class [ConsentDetail](-consent-detail/index.md)(val consent: [PaymentConsent](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-consent/index.md)) : [CardSectionType](index.md) |
| [ConsentList](-consent-list/index.md) | [androidJvm]<br>object [ConsentList](-consent-list/index.md) : [CardSectionType](index.md) |

## Properties

| Name | Summary |
|---|---|
| [buttonTitleRes](button-title-res.md) | [androidJvm]<br>abstract val [buttonTitleRes](button-title-res.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [screenTitleRes](screen-title-res.md) | [androidJvm]<br>abstract val [screenTitleRes](screen-title-res.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? |
