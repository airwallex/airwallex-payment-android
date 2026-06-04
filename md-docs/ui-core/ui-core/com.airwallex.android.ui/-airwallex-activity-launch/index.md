//[ui-core](../../../index.md)/[com.airwallex.android.ui](../index.md)/[AirwallexActivityLaunch](index.md)

# AirwallexActivityLaunch

[androidJvm]\
abstract class [AirwallexActivityLaunch](index.md)&lt;[TargetActivity](index.md) : [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html), [ArgsType](index.md) : [AirwallexActivityLaunch.Args](-args/index.md)&gt;(originalActivity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html), fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html)? = null, targetActivity: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[TargetActivity](index.md)&gt;, requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html))

## Constructors

| | |
|---|---|
| [AirwallexActivityLaunch](-airwallex-activity-launch.md) | [androidJvm]<br>constructor(activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html), targetClass: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[TargetActivity](index.md)&gt;, requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html))constructor(fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html), targetClass: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[TargetActivity](index.md)&gt;, requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html))constructor(originalActivity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html), fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html)? = null, targetActivity: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[TargetActivity](index.md)&gt;, requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |

## Types

| Name | Summary |
|---|---|
| [Args](-args/index.md) | [androidJvm]<br>interface [Args](-args/index.md) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |
| [Result](-result/index.md) | [androidJvm]<br>interface [Result](-result/index.md) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |

## Functions

| Name | Summary |
|---|---|
| [launchForResult](launch-for-result.md) | [androidJvm]<br>fun [launchForResult](launch-for-result.md)(args: [ArgsType](index.md), resultCallBack: (requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), result: [ActivityResult](https://developer.android.com/reference/kotlin/androidx/activity/result/ActivityResult.html)) -&gt; [Unit](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-unit/index.html)) |
