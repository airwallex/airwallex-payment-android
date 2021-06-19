package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Bank(val value: String, val drawableRes: Int, val displayName: String, val currency: String) : Parcelable {

    BANGKOK("bangkokbank", R.drawable.airwallex_ic_bangkok_bank, "Bangkok Bank", "THB"),
    KRUNGSRI("krungsri", R.drawable.airwallex_ic_krungsri_bank, "Krungsri Bank", "THB"),
    KRUNG_THAI("krungthaibank", R.drawable.airwallex_ic_krung_thai_bank, "Krung Thai Bank", "THB"),
    SIAM_COMMERCIAL("siamcommercialbank", R.drawable.airwallex_ic_siam_commercial_bank, "Siam Commercial Bank", "THB"),
    BANK_MANDIRI("bankmandiri", R.drawable.airwallex_ic_bank_mandiri, "Bank Mandiri", "IDR"),
    BANK_DANAMON("bankdanamon", R.drawable.airwallex_ic_bank_danamon, "Bank Danamon", "IDR"),
    CIMB_NIAGA("cimbniaga", R.drawable.airwallex_ic_cimb_bank, "CIMB Niaga", "IDR"),
    PERMATA("permata", R.drawable.airwallex_ic_permata, "Permata", "IDR"),
    MAY_BANK("maybank", R.drawable.airwallex_ic_maybank, "MayBank", "IDR");

    internal companion object {
        internal fun fromValue(value: String?): Bank? {
            return values().firstOrNull { it.value == value }
        }
    }
}
