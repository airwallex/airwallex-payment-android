//[components-core](../../../index.md)/[com.airwallex.android.core.model.parser](../index.md)/[PaymentMethodParser](index.md)

# PaymentMethodParser

[androidJvm]\
class [PaymentMethodParser](index.md) : [ModelJsonParser](../-model-json-parser/index.md)&lt;[PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md)&gt;

## Constructors

| | |
|---|---|
| [PaymentMethodParser](-payment-method-parser.md) | [androidJvm]<br>constructor() |

## Types

| Name | Summary |
|---|---|
| [CardParser](-card-parser/index.md) | [androidJvm]<br>class [CardParser](-card-parser/index.md) : [ModelJsonParser](../-model-json-parser/index.md)&lt;[PaymentMethod.Card](../../com.airwallex.android.core.model/-payment-method/-card/index.md)&gt; |
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |
| [GooglePayParser](-google-pay-parser/index.md) | [androidJvm]<br>class [GooglePayParser](-google-pay-parser/index.md) : [ModelJsonParser](../-model-json-parser/index.md)&lt;[PaymentMethod.GooglePay](../../com.airwallex.android.core.model/-payment-method/-google-pay/index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [dateFormat](../-model-json-parser/date-format.md) | [androidJvm]<br>open val [dateFormat](../-model-json-parser/date-format.md): [SimpleDateFormat](https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat.html) |

## Functions

| Name | Summary |
|---|---|
| [parse](parse.md) | [androidJvm]<br>open override fun [parse](parse.md)(json: [JSONObject](https://developer.android.com/reference/kotlin/org/json/JSONObject.html)): [PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md) |
