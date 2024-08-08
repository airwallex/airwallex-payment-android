package com.airwallex.paymentacceptance.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import kotlin.reflect.KClass

fun <T : Activity> Context.startActivity(clazz: KClass<T>) {
    startActivity(Intent(this, clazz.java))
}