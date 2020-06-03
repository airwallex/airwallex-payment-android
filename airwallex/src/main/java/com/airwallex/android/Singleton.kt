package com.airwallex.android

abstract class Singleton<T> {
    private var instance: T? = null

    protected abstract fun create(): T

    fun get(): T? {
        synchronized(this) {
            if (instance == null) {
                instance = create()
            }
            return instance
        }
    }
}
