package com.airwallex.android.core

/**
 * Specifies the browser behavior for redirect-based payment methods.
 */
enum class RedirectMode {
    /**
     * Default Chrome Custom Tabs behavior.
     */
    CUSTOM_TAB,

    /**
     * Chrome Custom Tab showing a full screen bottom sheet.
     */
    CUSTOM_TAB_BOTTOM_SHEET,

    /**
     * Open in an external browser app instead of Custom Tabs.
     */
    EXTERNAL_BROWSER
}
