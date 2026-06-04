//[ui-core](../../../index.md)/[com.airwallex.android.ui.composables](../index.md)/[AirwallexThemeConfig](index.md)

# AirwallexThemeConfig

[androidJvm]\
object [AirwallexThemeConfig](index.md)

Global theme configuration for Airwallex SDK UI. Configure theme using setThemeColor() and setDarkMode(). Context is initialized lazily when AirwallexTheme is first rendered.

## Properties

| Name | Summary |
|---|---|
| [isDarkTheme](is-dark-theme.md) | [androidJvm]<br>val [isDarkTheme](is-dark-theme.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Current dark theme state. Resolves SYSTEM mode to actual boolean. Priority: programmatic config system setting |
| [themeColor](theme-color.md) | [androidJvm]<br>val [themeColor](theme-color.md): [Color](https://developer.android.com/reference/kotlin/androidx/compose/ui/graphics/Color.html)<br>Current theme color. Priority: programmatic config resource default |

## Functions

| Name | Summary |
|---|---|
| [initializeContext](initialize-context.md) | [androidJvm]<br>fun [initializeContext](initialize-context.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)) |
| [setDarkMode](set-dark-mode.md) | [androidJvm]<br>fun [setDarkMode](set-dark-mode.md)(darkMode: [DarkMode](../-dark-mode/index.md))<br>Set dark mode preference. |
| [setThemeColor](set-theme-color.md) | [androidJvm]<br>fun [setThemeColor](set-theme-color.md)(color: [Color](https://developer.android.com/reference/kotlin/androidx/compose/ui/graphics/Color.html))<br>[androidJvm]<br>fun [setThemeColor](set-theme-color.md)(colorString: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Set theme color using hex color string. Supported formats: &quot;#RRGGBB&quot;, &quot;#AARRGGBB&quot;, &quot;0xRRGGBB&quot;, &quot;0xAARRGGBB&quot; |
