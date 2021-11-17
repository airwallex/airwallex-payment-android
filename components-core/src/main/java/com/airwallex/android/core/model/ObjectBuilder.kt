package com.airwallex.android.core.model

interface ObjectBuilder<ObjectType> {
    fun build(): ObjectType
}
