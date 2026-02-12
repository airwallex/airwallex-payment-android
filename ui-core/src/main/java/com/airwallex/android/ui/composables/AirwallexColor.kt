package com.airwallex.android.ui.composables

import androidx.compose.ui.graphics.Color

object AirwallexColor {
    enum class Level(val value: Int) {
        Level5(5), Level10(10), Level20(20), Level30(30), Level40(40),
        Level50(50), Level60(60), Level70(70), Level80(80), Level90(90), Level100(100)
    }

    val Transparent = Color(0x00000000)
    val Black = Color(red = 0, green = 0, blue = 0)
    val White = Color(red = 255, green = 255, blue = 255)
    val Grey100 = Color(0xff1A1D21)
    val Gray100 = Color(0xFF1A1D21)
    val Gray90 = Color(0xFF2F3237)
    val Gray80 = Color(0xFF42474D)
    val Gray70 = Color(0xFF545B63)
    val Gray60 = Color(0xFF6C747F)
    val Gray50 = Color(0xFF868E98)
    val Gray40 = Color(0xFFB0B6BF)
    val Gray30 = Color(0xFFD7DBE0)
    val Gray20 = Color(0xFFE8EAED)
    val Gray10 = Color(0xFFF6F7F8)
    val Gray5 = Color(0xFFFBFBFC)

    val Ultraviolet10 = Color(0xFFF0EFFF)
    val Ultraviolet20 = Color(0xFFDFDEFF)
    val Ultraviolet30 = Color(0xFFD0CDFF)
    val Ultraviolet40 = Color(0xFFB3AEFF)
    val Ultraviolet50 = Color(0xFF9585FF)
    val Ultraviolet60 = Color(0xFF775CFF)
    val Ultraviolet70 = Color(0xFF612FFF)
    val Ultraviolet80 = Color(0xFF4F00D6)
    val Ultraviolet90 = Color(0xFF30008F)
    val Ultraviolet100 = Color(0xFF15005C)

    val Orange10 = Color(0xFFFFEDE0)
    val Orange30 = Color(0xFFFFD0AD)
    val Orange50 = Color(0xFFFF8E3C)
    val Orange70 = Color(0xFFD68100)
    val Orange90 = Color(0xFFB87400)

    val Yellow10 = Color(0xFFFFF8E0)
    val Yellow30 = Color(0xFFFFECAD)
    val Yellow50 = Color(0xFFFFD014)
    val Yellow70 = Color(0xFFE0BB00)
    val Yellow90 = Color(0xFFC2A100)

    val Green10 = Color(0xFFE0F7E7)
    val Green30 = Color(0xFFB1FBB1)
    val Green50 = Color(0xFF0BEA82)
    val Green70 = Color(0xFF08AF61)
    val Green90 = Color(0xFF067F46)

    val Red10 = Color(0xFFFFE0E0)
    val Red30 = Color(0xFFFFADAD)
    val Red40 = Color(0xFFFF7A70)
    val Red50 = Color(0xFFFF4F42)
    val Red60 = Color(0xFFD93026)
    val Red70 = Color(0xFFB80D00)
    val Red90 = Color(0xFF990000)

    val Blue10 = Color(0xFFE0F2FE)

    val TextPrimary = Color(0xFF14171A)
    val TextSecondary = Color(0xFF68707A)
    val BackgroundSecondary = Color(0xFFF5F6F7)
    val ProgressbarStart = Color(0xFFFF4F42)
    val Interactive = Color(0xFF612FFF)
    val TextError = Color(0xFFD91807)
    val ProgressbarDefault = Color(0xFFB0B6BF)
    val WarningBackground = Color(0xFFFFF6EF)
    val ErrorBox = Color(0xFFFFE9E6)
    val LoadingBarBackgroundColor = Color(0xF7F6F7F8)
    val LoadingBarFillColor = Color(0xFFEBECF0)
    val LoadingBarEdgeFadeColor = Color(0x00EBECF0)

    @Suppress("ComplexMethod")
    fun Color.adjustByLevel(level: Level): Color {
        val base = 70

        if (this == Ultraviolet70) {
            return when (level) {
                Level.Level5 -> Ultraviolet10
                Level.Level10 -> Ultraviolet10
                Level.Level20 -> Ultraviolet20
                Level.Level30 -> Ultraviolet30
                Level.Level40 -> Ultraviolet40
                Level.Level50 -> Ultraviolet50
                Level.Level60 -> Ultraviolet60
                Level.Level70 -> Ultraviolet70
                Level.Level80 -> Ultraviolet80
                Level.Level90 -> Ultraviolet90
                Level.Level100 -> Ultraviolet100
            }
        }

        return when {
            level.value < base -> {
                val fraction = (base - level.value).toFloat() / base
                androidx.compose.ui.graphics.lerp(this, White, fraction)
            }
            level.value > base -> {
                val fraction = (level.value - base).toFloat() / 50f
                androidx.compose.ui.graphics.lerp(this, Black, fraction)
            }
            else -> this
        }
    }

    // ============================================================================
    // Semantic Colors
    // ============================================================================

    fun theme(): Color {
        val isDark = AirwallexThemeConfig.isDarkTheme
        val themeColor = AirwallexThemeConfig.themeColor
        return themeColor.adjustByLevel(
            if (isDark) Level.Level40 else Level.Level70
        )
    }

    // Background Colors
    fun backgroundPrimary(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray100 else White

    fun backgroundSecondary(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray90 else Gray10

    fun backgroundField(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray90 else Gray5

    fun backgroundHighlight(): Color {
        val isDark = AirwallexThemeConfig.isDarkTheme
        val themeColor = AirwallexThemeConfig.themeColor
        return themeColor.adjustByLevel(
            if (isDark) Level.Level90 else Level.Level5
        )
    }

    fun backgroundSelected(): Color {
        val isDark = AirwallexThemeConfig.isDarkTheme
        val themeColor = AirwallexThemeConfig.themeColor
        return themeColor.adjustByLevel(
            if (isDark) Level.Level80 else Level.Level20
        )
    }

    fun backgroundInteractive(): Color = theme()

    fun backgroundWarning(): Color = Yellow10

    // Border Colors
    fun borderDecorative(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray80 else Gray20

    fun borderPerceivable(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray60 else Gray50

    fun borderInteractive(): Color = theme()

    fun borderError(): Color = if (AirwallexThemeConfig.isDarkTheme) Red60 else Red50

    // Icon Colors
    fun iconPrimary(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray30 else Gray80

    fun iconSecondary(): Color = Gray50

    fun iconLink(): Color = theme()

    fun iconDisabled(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray70 else Gray40

    fun iconWarning(): Color = Orange50

    // Text Colors
    fun textLink(): Color = theme()

    fun textPrimary(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray10 else Gray100

    fun textSecondary(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray50 else Gray60

    fun textPlaceholder(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray60 else Gray50

    fun textError(): Color = if (AirwallexThemeConfig.isDarkTheme) Red40 else Red60

    fun textInverse(): Color = if (AirwallexThemeConfig.isDarkTheme) Gray100 else White
}