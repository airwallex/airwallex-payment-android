//[ui-core](../../index.md)/[com.airwallex.android.ui](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [AirwallexActivity](-airwallex-activity/index.md) | [androidJvm]<br>abstract class [AirwallexActivity](-airwallex-activity/index.md) : [AppCompatActivity](https://developer.android.com/reference/kotlin/androidx/appcompat/app/AppCompatActivity.html), [AirwallexInternalActivity](../../../components-core/components-core/com.airwallex.android.core/-airwallex-internal-activity/index.md) |
| [AirwallexActivityLaunch](-airwallex-activity-launch/index.md) | [androidJvm]<br>abstract class [AirwallexActivityLaunch](-airwallex-activity-launch/index.md)&lt;[TargetActivity](-airwallex-activity-launch/index.md) : [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html), [ArgsType](-airwallex-activity-launch/index.md) : [AirwallexActivityLaunch.Args](-airwallex-activity-launch/-args/index.md)&gt;(originalActivity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html), fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html)? = null, targetActivity: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[TargetActivity](-airwallex-activity-launch/index.md)&gt;, requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
| [AirwallexActivityLaunchLifecycleCallbacks](-airwallex-activity-launch-lifecycle-callbacks/index.md) | [androidJvm]<br>abstract class [AirwallexActivityLaunchLifecycleCallbacks](-airwallex-activity-launch-lifecycle-callbacks/index.md) : [Application.ActivityLifecycleCallbacks](https://developer.android.com/reference/kotlin/android/app/Application.ActivityLifecycleCallbacks.html) |
| [AirwallexActivityLaunchResultCallback](-airwallex-activity-launch-result-callback/index.md) | [androidJvm]<br>abstract class [AirwallexActivityLaunchResultCallback](-airwallex-activity-launch-result-callback/index.md) : [ActivityResultCallback](https://developer.android.com/reference/kotlin/androidx/activity/result/ActivityResultCallback.html)&lt;[ActivityResult](https://developer.android.com/reference/kotlin/androidx/activity/result/ActivityResult.html)&gt; |
| [AirwallexLoadingDialogFragment](-airwallex-loading-dialog-fragment/index.md) | [androidJvm]<br>class [AirwallexLoadingDialogFragment](-airwallex-loading-dialog-fragment/index.md) : [DialogFragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/DialogFragment.html)<br>DialogFragment-based loading dialog that handles configuration changes properly. Shows a transparent loading indicator with dimmed background. |
| [AirwallexWebView](-airwallex-web-view/index.md) | [androidJvm]<br>class [AirwallexWebView](-airwallex-web-view/index.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), attrs: [AttributeSet](https://developer.android.com/reference/kotlin/android/util/AttributeSet.html)?) : [WebView](https://developer.android.com/reference/kotlin/android/webkit/WebView.html) |

## Functions

| Name | Summary |
|---|---|
| [destroyWebView](destroy-web-view.md) | [androidJvm]<br>fun [WebView](https://developer.android.com/reference/kotlin/android/webkit/WebView.html).[destroyWebView](destroy-web-view.md)() |
