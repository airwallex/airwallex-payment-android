package com.airwallex.android.core.util

private val E164_REGEX = Regex("^\\+?[1-9]\\d{1,14}$")

/**
 * E.164: leading `+` or start immediately, country digit 1-9,
 * then 1-14 more digits (15 max total). Whitespace and formatting characters
 * are NOT allowed — callers should strip them first if needed.
 */
fun String.isValidE164Phone(): Boolean = E164_REGEX.matches(this)
