//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[GooglePayOptions](index.md)/[allowedCardNetworks](allowed-card-networks.md)

# allowedCardNetworks

[androidJvm]\
val [allowedCardNetworks](allowed-card-networks.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;

The card networks that your application accepts

#### See also

| | |
|---|---|
| [GooglePayOptions.allowedCardNetworks](allowed-card-networks.md) | (https://developers.google.com/pay/api/android/reference/request-objects#CardParameters) So far, we have supported the following types: AMEX, DISCOVER, JCB, MASTERCARD, VISA, MAESTRO. If you do not set this list, we will default to supporting these types: AMEX, DISCOVER, JCB, MASTERCARD, VISA. If you want to support MAESTRO, set the value for allowedCardNetworks. Note that if you add MAESTRO, your countryCode must include BR. If you include types that we do not support, the payment process will fail. |
