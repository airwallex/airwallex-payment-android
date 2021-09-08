package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Bank(val value: String, val displayName: String, val currency: String) : Parcelable {

    BANGKOK("bangkok", "Bangkok Bank", "THB"),
    KRUNGSRI("krungsri", "Krungsri Bank", "THB"),
    KRUNG_THAI("krungthai", "Krung Thai Bank", "THB"),
    SIAM_COMMERCIAL("siam_commercial", "Siam Commercial Bank", "THB"),
    BANK_MANDIRI("mandiri", "Bank Mandiri", "IDR"),
    BANK_DANAMON("danamon", "Bank Danamon", "IDR"),
    CIMB_NIAGA("cimb_niaga", "CIMB Niaga", "IDR"),
    PERMATA("permata", "Permata", "IDR"),
    MAY_BANK("maybank", "MayBank", "IDR");

    companion object {
        fun fromValue(value: String?): Bank? {
            return values().firstOrNull { it.value == value }
        }
    }
}
