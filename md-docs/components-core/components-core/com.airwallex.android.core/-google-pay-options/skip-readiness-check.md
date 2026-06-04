//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[GooglePayOptions](index.md)/[skipReadinessCheck](skip-readiness-check.md)

# skipReadinessCheck

[androidJvm]\
val [skipReadinessCheck](skip-readiness-check.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = false

Setting skipReadinessCheck to true will skip the IsReadyToPay check before invoking Google Pay.

#### See also

| | |
|---|---|
| IsReadyToPay | (https://developers.google.com/pay/api/android/reference/client#isReadyToPay) On certain models from brands like Xiaomi and Honor, IsReadyToPay always returns false, which can block Google Pay. Skipping this check will try to launch Google Pay sheet directly. We advise you to carefully consider whether to skip this step. |
