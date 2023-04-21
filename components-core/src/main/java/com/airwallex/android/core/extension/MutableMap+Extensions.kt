package com.airwallex.android.core.extension

fun <K, V> MutableMap<K, V>.putIfNotNull(key: K, value: V?) {
    if (value != null) {
        put(key, value)
    }
}