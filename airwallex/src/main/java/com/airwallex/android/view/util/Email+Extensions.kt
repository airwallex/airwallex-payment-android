package com.airwallex.android.view.util

import android.util.Patterns

fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()