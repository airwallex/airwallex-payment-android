//[ui-core](../../../index.md)/[com.airwallex.android.ui](../index.md)/[AirwallexActivityLaunchResultCallback](index.md)

# AirwallexActivityLaunchResultCallback

[androidJvm]\
abstract class [AirwallexActivityLaunchResultCallback](index.md) : [ActivityResultCallback](https://developer.android.com/reference/kotlin/androidx/activity/result/ActivityResultCallback.html)&lt;[ActivityResult](https://developer.android.com/reference/kotlin/androidx/activity/result/ActivityResult.html)&gt;

## Constructors

| | |
|---|---|
| [AirwallexActivityLaunchResultCallback](-airwallex-activity-launch-result-callback.md) | [androidJvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [getRequestCode](get-request-code.md) | [androidJvm]<br>fun [getRequestCode](get-request-code.md)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? |
| [getResultCallback](get-result-callback.md) | [androidJvm]<br>fun [getResultCallback](get-result-callback.md)(): (requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), result: [ActivityResult](https://developer.android.com/reference/kotlin/androidx/activity/result/ActivityResult.html)) -&gt; [Unit](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-unit/index.html)? |
| [onActivityResult](index.md#595400627%2FFunctions%2F-1628350927) | [androidJvm]<br>abstract fun [onActivityResult](index.md#595400627%2FFunctions%2F-1628350927)(result: [ActivityResult](https://developer.android.com/reference/kotlin/androidx/activity/result/ActivityResult.html)) |
| [setRequestCode](set-request-code.md) | [androidJvm]<br>fun [setRequestCode](set-request-code.md)(requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
| [setResultCallback](set-result-callback.md) | [androidJvm]<br>fun [setResultCallback](set-result-callback.md)(callback: (requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), result: [ActivityResult](https://developer.android.com/reference/kotlin/androidx/activity/result/ActivityResult.html)) -&gt; [Unit](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-unit/index.html)) |
