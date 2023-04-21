package com.airwallex.android.core.extension

fun String.capitalized() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun String.splitByUppercaseWithSeparator(separator: String): String {
    return this.replace(Regex("([a-z])([A-Z])"), "$1$separator$2")
}