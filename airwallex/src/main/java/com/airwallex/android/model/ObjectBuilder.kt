package com.airwallex.android.model

internal interface ObjectBuilder<ObjectType> {
    fun build(): ObjectType
}
