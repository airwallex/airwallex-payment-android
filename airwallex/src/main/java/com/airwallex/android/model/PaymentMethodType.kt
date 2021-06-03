package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.R
import kotlinx.parcelize.Parcelize

/**
 * The type of [PaymentMethod]
 */
@Parcelize
enum class PaymentMethodType(val value: String, val drawableRes: Int = 0, val displayName: String = "") : Parcelable {

    CARD("card"),
    ALIPAY_CN("alipaycn", R.drawable.airwallex_ic_alipay_cn, "Alipay"),
    ALIPAY_HK("alipayhk", R.drawable.airwallex_ic_alipay_hk, "AlipayHK"),
    WECHAT("wechatpay", R.drawable.airwallex_ic_wechat, "WeChat Pay"),
    DANA("dana", R.drawable.airwallex_ic_dana, "DANA"),
    GCASH("gcash", R.drawable.airwallex_ic_gcash, "GCash"),
    KAKAOPAY("kakaopay", R.drawable.airwallex_ic_kakao_pay, "Kakao Pay"),
    TNG("tng", R.drawable.airwallex_ic_touchngo, "Touch 'n Go"),
    TRUE_MONEY("truemoney", R.drawable.airwallex_ic_truemoney, "Truemoney"),
    BKASH("bkash", R.drawable.airwallex_ic_bkash, "bKash"),
    POLI("poli", 0, "POLi"),
    FPX("fpx", 0, "FPX");

    internal companion object {
        internal fun fromValue(value: String?): PaymentMethodType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
