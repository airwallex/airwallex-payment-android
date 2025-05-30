package com.airwallex.android.ui.composables

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.airwallex.android.ui.R

enum class AirwallexTypography {
    LargeTitleBold,
    LargeTitle,
    Title100,
    Title200,
    Title300,
    Headline100,
    Headline200,
    Subtitle100,
    Subtitle100Bold,
    Body100,
    Body100Bold,
    Body200,
    Body200Bold,
    Caption100,
    Caption100Bold,
    Caption200,
    Caption200Bold,
    Caption300,
    Caption300Bold,
    Optional100,
    Optional200;

    fun toComposeTextStyle(): TextStyle {
        return when (this) {
            LargeTitleBold -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 41.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            LargeTitle-> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontSize = 34.sp,
                lineHeight = 41.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Title100 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Title200 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Title300 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 25.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Headline100 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Headline200 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Subtitle100 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Subtitle100Bold -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Body100 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Body100Bold -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Body200 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Body200Bold -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Caption100 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Caption100Bold -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Caption200 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Caption200Bold -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Caption300 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Caption300Bold -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Optional100 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
            Optional200 -> TextStyle(
                fontFamily = CircularXxFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
        }
    }
}

val CircularXxFontFamily = FontFamily(
    Font(R.font.circular_xx_bold, FontWeight.Bold),
    Font(R.font.circular_xx_medium, FontWeight.Medium),
    Font(R.font.circular_xx_regular, FontWeight.Normal),
)