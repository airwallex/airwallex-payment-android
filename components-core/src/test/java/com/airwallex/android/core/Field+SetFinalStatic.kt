package com.airwallex.android.core

import java.lang.reflect.Field
import java.lang.reflect.Modifier

fun Field.setFinalStatic(newValue: Any) {
    isAccessible = true

    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(this, modifiers and Modifier.FINAL.inv())

    set(null, newValue)
}