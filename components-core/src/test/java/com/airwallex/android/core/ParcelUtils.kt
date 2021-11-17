package com.airwallex.android.core

import android.os.Bundle
import android.os.Parcelable

internal object ParcelUtils {

    internal fun <Source : Parcelable> create(
        source: Source
    ): Source {
        val bundle = Bundle()
        bundle.putParcelable(KEY, source)
        return requireNotNull(bundle.getParcelable(KEY))
    }

    internal fun <Source : Parcelable?> createMaybeNull(
        source: Source
    ): Source {
        val bundle = Bundle()
        bundle.putParcelable(KEY, source)
        return requireNotNull(bundle.getParcelable(KEY))
    }

    private const val KEY = "parcelable"
}
