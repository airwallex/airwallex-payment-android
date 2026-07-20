//[components-core](../../index.md)/[com.airwallex.android.core.model](index.md)/[withMaestroIfMasterCard](with-maestro-if-master-card.md)

# withMaestroIfMasterCard

[androidJvm]\
fun [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[CardScheme](-card-scheme/index.md)&gt;.[withMaestroIfMasterCard](with-maestro-if-master-card.md)(): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[CardScheme](-card-scheme/index.md)&gt;

Maestro is a debit sub-brand of Mastercard that the backend does not return as a separate card scheme, so append it whenever Mastercard is supported and Maestro is not already present.
