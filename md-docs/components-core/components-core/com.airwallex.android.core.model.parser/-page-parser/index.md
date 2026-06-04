//[components-core](../../../index.md)/[com.airwallex.android.core.model.parser](../index.md)/[PageParser](index.md)

# PageParser

[androidJvm]\
class [PageParser](index.md)&lt;[T](index.md)&gt;(itemParser: [ModelJsonParser](../-model-json-parser/index.md)&lt;[T](index.md)&gt;) : [ModelJsonParser](../-model-json-parser/index.md)&lt;[Page](../../com.airwallex.android.core.model/-page/index.md)&lt;[T](index.md)&gt;&gt;

## Constructors

| | |
|---|---|
| [PageParser](-page-parser.md) | [androidJvm]<br>constructor(itemParser: [ModelJsonParser](../-model-json-parser/index.md)&lt;[T](index.md)&gt;) |

## Properties

| Name | Summary |
|---|---|
| [dateFormat](../-model-json-parser/date-format.md) | [androidJvm]<br>open val [dateFormat](../-model-json-parser/date-format.md): [SimpleDateFormat](https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat.html) |

## Functions

| Name | Summary |
|---|---|
| [parse](parse.md) | [androidJvm]<br>open override fun [parse](parse.md)(json: [JSONObject](https://developer.android.com/reference/kotlin/org/json/JSONObject.html)): [Page](../../com.airwallex.android.core.model/-page/index.md)&lt;[T](index.md)&gt; |
