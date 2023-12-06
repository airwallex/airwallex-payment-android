package com.airwallex.android.core.model

interface Page<T> : AirwallexModel {
    var items: List<T>
    val hasMore: Boolean
}