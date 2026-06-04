//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[Appearance](index.md)

# Appearance

data class [Appearance](index.md)(@[ColorInt](https://developer.android.com/reference/kotlin/androidx/annotation/ColorInt.html)val themeColor: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, val isDarkTheme: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Payment UI appearance configuration. Use this to customize the theme color and dark mode preference for the Airwallex SDK UI.

#### Parameters

androidJvm

| | |
|---|---|
| themeColor | Optional theme color in ARGB format (e.g., 0xFF612FFF) |
| isDarkTheme | Optional dark theme preference. If null, follows system setting |

## Constructors

| | |
|---|---|
| [Appearance](-appearance.md) | [androidJvm]<br>constructor(@[ColorInt](https://developer.android.com/reference/kotlin/androidx/annotation/ColorInt.html)themeColor: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, isDarkTheme: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null) |

## Properties

| Name | Summary |
|---|---|
| [isDarkTheme](is-dark-theme.md) | [androidJvm]<br>val [isDarkTheme](is-dark-theme.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null |
| [themeColor](theme-color.md) | [androidJvm]<br>val [themeColor](theme-color.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../com.airwallex.android.core.model/-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../com.airwallex.android.core.model/-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../../com.airwallex.android.core.model/-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../com.airwallex.android.core.model/-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
