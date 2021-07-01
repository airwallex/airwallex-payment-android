package com.airwallex.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class PaymentMethodRequiredField(val key: String) : Parcelable {

    BANK_NAME("bank_name"),

    SHOPPER_NAME("shopper_name"),

    SHOPPER_EMAIL("shopper_email"),

    SHOPPER_PHONE("shopper_phone");
}
