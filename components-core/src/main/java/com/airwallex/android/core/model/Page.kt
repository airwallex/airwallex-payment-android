package com.airwallex.android.core.model

interface Page<T> : AirwallexModel {
    val items: List<T>
    val hasMore: Boolean
}