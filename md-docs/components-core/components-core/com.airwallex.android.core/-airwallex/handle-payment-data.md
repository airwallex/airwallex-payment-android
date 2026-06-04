//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[Airwallex](index.md)/[handlePaymentData](handle-payment-data.md)

# handlePaymentData

[androidJvm]\
fun [~~handlePaymentData~~](handle-payment-data.md)(requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), resultCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), data: [Intent](https://developer.android.com/reference/kotlin/android/content/Intent.html)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)

---

### Deprecated

This method will be removed in future versions

---

Method to handle Activity results from Airwallex activities. Pass data here from your host's `#onActivityResult(int, int, Intent)` function.

#### Return

`true` if the activity result was handled by this function, otherwise `false`

#### Parameters

androidJvm

| | |
|---|---|
| requestCode | the request code used to open the resulting activity |
| resultCode | a result code representing the success of the intended action |
| data | an [Intent](https://developer.android.com/reference/kotlin/android/content/Intent.html) with the resulting data from the Activity |
