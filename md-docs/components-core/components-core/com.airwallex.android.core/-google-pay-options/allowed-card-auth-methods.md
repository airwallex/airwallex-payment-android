//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[GooglePayOptions](index.md)/[allowedCardAuthMethods](allowed-card-auth-methods.md)

# allowedCardAuthMethods

[androidJvm]\
val [allowedCardAuthMethods](allowed-card-auth-methods.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null

The Google Pay API may return cards on file on Google.com (PAN_ONLY) and/or a device token on an Android device authenticated with a 3-D Secure cryptogram (CRYPTOGRAM_3DS). Default: Both authentication methods are allowed.
