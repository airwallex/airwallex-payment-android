package com.airwallex.android.view.util

fun String.isValidEmail() = Regex("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}").matches(this)