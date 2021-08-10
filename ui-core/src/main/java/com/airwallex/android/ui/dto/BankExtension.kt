package com.airwallex.android.ui.dto

import androidx.annotation.DrawableRes
import com.airwallex.android.core.model.Bank
import com.airwallex.android.ui.R

@get:DrawableRes
val Bank.drawableRes: Int
    get() {
        return when (this) {
            Bank.BANGKOK -> R.drawable.airwallex_ic_bangkok_bank
            Bank.KRUNGSRI -> R.drawable.airwallex_ic_krungsri_bank
            Bank.KRUNG_THAI -> R.drawable.airwallex_ic_krung_thai_bank
            Bank.SIAM_COMMERCIAL -> R.drawable.airwallex_ic_siam_commercial_bank
            Bank.BANK_MANDIRI -> R.drawable.airwallex_ic_bank_mandiri
            Bank.BANK_DANAMON -> R.drawable.airwallex_ic_bank_danamon
            Bank.CIMB_NIAGA -> R.drawable.airwallex_ic_cimb_bank
            Bank.PERMATA -> R.drawable.airwallex_ic_permata
            Bank.MAY_BANK -> R.drawable.airwallex_ic_maybank
        }
    }
