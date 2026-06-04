//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[CardNextActionModel](index.md)/[activityProvider](activity-provider.md)

# activityProvider

[androidJvm]\
val [activityProvider](activity-provider.md): () -&gt; [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/core/app/ComponentActivity.html)

#### Parameters

androidJvm

| | |
|---|---|
| activityProvider | Lambda function that provides the current activity reference.     This is crucial for handling configuration changes (e.g., screen rotation).     Instead of capturing a static activity reference that becomes stale after     configuration changes, this provider is called dynamically to always get     the current, valid activity instance. This ensures that activities launched     during async operations (like ThreeDSecurityActivity) use the correct activity     context even after multiple screen rotations. |
