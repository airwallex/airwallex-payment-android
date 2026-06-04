//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[RetrieveAvailablePaymentMethodParams](index.md)

# RetrieveAvailablePaymentMethodParams

[androidJvm]\
data class [RetrieveAvailablePaymentMethodParams](index.md)

The params that used for retrieve [PaymentMethod](../-payment-method/index.md)

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md)(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), pageNum: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) : [ObjectBuilder](../-object-builder/index.md)&lt;[RetrieveAvailablePaymentMethodParams](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [active](active.md) | [androidJvm]<br>val [active](active.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)?<br>Indicate whether the payment method type is active |
| [clientSecret](client-secret.md) | [androidJvm]<br>val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [countryCode](country-code.md) | [androidJvm]<br>val [countryCode](country-code.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The supported country code |
| [pageNum](page-num.md) | [androidJvm]<br>val [pageNum](page-num.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)<br>Page number starting from 0 |
| [pageSize](page-size.md) | [androidJvm]<br>val [pageSize](page-size.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)<br>Number of payment methods to be listed per page |
| [transactionCurrency](transaction-currency.md) | [androidJvm]<br>val [transactionCurrency](transaction-currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The supported transaction currency |
| [transactionMode](transaction-mode.md) | [androidJvm]<br>val [transactionMode](transaction-mode.md): [TransactionMode](../-transaction-mode/index.md)?<br>The supported transaction mode. One of oneoff, recurring. |
