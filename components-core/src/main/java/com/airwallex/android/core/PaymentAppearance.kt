package com.airwallex.android.core

import androidx.annotation.ColorInt

/**
 * Payment UI appearance configuration.
 * Use this to customize the theme color and dark mode preference for the Airwallex SDK UI.
 *
 * @param themeColor Optional theme color in ARGB format (e.g., 0xFF612FFF)
 * @param isDarkTheme Optional dark theme preference. If null, follows system setting
 */
data class PaymentAppearance(
    @ColorInt val themeColor: Int? = null,
    val isDarkTheme: Boolean? = null
)
