package com.airwallex.android.view.util

import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.CardScheme

fun List<CardScheme>.toSupportedIcons(): List<Int> {
    return mapNotNull { CardBrand.fromType(it.name)?.icon }
}