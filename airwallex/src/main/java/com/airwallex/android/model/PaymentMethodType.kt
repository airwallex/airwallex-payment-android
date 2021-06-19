package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.R
import kotlinx.parcelize.Parcelize

/**
 * The type of [PaymentMethod]
 */
@Parcelize
enum class PaymentMethodType(val value: String, val drawableRes: Int = 0, val displayName: String = "", val classify: PaymentMethodClassify) : Parcelable {

    CARD("card", 0, "", PaymentMethodClassify.CARD),
    WECHAT("wechatpay", R.drawable.airwallex_ic_wechat, "WeChat Pay", PaymentMethodClassify.WECHAT),

    // Redirect_method
    // * ALIPAY *
    ALIPAY_CN("alipaycn", R.drawable.airwallex_ic_alipay_cn, "Alipay", PaymentMethodClassify.ALIPAY),
    ALIPAY_HK("alipayhk", R.drawable.airwallex_ic_alipay_hk, "AlipayHK", PaymentMethodClassify.ALIPAY),
    TRUE_MONEY("truemoney", R.drawable.airwallex_ic_truemoney, "Truemoney", PaymentMethodClassify.ALIPAY),
    BKASH("bkash", R.drawable.airwallex_ic_bkash, "bKash", PaymentMethodClassify.ALIPAY),
    GCASH("gcash", R.drawable.airwallex_ic_gcash, "GCash", PaymentMethodClassify.ALIPAY),
    DANA("dana", R.drawable.airwallex_ic_dana, "DANA", PaymentMethodClassify.ALIPAY),
    KAKAOPAY("kakaopay", R.drawable.airwallex_ic_kakao_pay, "Kakao Pay", PaymentMethodClassify.ALIPAY),
    TNG("tng", R.drawable.airwallex_ic_touchngo, "Touch 'n Go", PaymentMethodClassify.ALIPAY),

    // * PPRO *
    // PHP
    DRAGON_PAY("dragonpay", 0, "DragonPay", PaymentMethodClassify.PPRO),

    // IDR
    PERMATANET("permatanet", R.drawable.airwallex_ic_permatanet, "Permatanet", PaymentMethodClassify.PPRO),
    ALFAMART("alfamart", R.drawable.airwallex_ic_alfamart, "Alfamart", PaymentMethodClassify.PPRO),
    INDOMARET("indomaret", R.drawable.airwallex_ic_indomaret, "Indomaret", PaymentMethodClassify.PPRO),
    DOKU_WALLET("doku_ewallet", R.drawable.airwallex_ic_doku_wallet, "DOKU Wallet", PaymentMethodClassify.PPRO),

    // MYR
    FPX("fpx", R.drawable.airwallex_ic_fpx, "FPX", PaymentMethodClassify.PPRO),
    SEVEN_ELEVEN("seven_eleven", R.drawable.airwallex_ic_eleven, "7eleven", PaymentMethodClassify.PPRO),

    // AUD
    POLI("poli", R.drawable.airwallex_ic_poli, "POLi", PaymentMethodClassify.PPRO),

    // THB
    TESCO_LOTUS("tesco_lotus", R.drawable.airwallex_ic_tesco_lotus_cash, "Tesco Lotus", PaymentMethodClassify.PPRO),

    // SGD
    E_NETS("enets", R.drawable.airwallex_ic_enets, "eNETS", PaymentMethodClassify.PPRO),
    GRAB_PAY("grabpay", R.drawable.airwallex_ic_grabpay, "Grabpay", PaymentMethodClassify.PPRO),

    // JPY
    PAY_EASY("payeasy", R.drawable.airwallex_ic_pay_easy, "Pay-easy", PaymentMethodClassify.PPRO),
    KONBINI("konbini", R.drawable.airwallex_ic_konbini, "Konbini", PaymentMethodClassify.PPRO),

    // GBP
    SKRILL("skrill", R.drawable.airwallex_ic_skrill, "Skrill", PaymentMethodClassify.PPRO),

    // IDR, THB
    BANK_TRANSFER("bank_transfer", R.drawable.airwallex_ic_bank_transfer, "Bank transfer", PaymentMethodClassify.PPRO),
    // THB
    ONLINE_BANKING("online_banking", R.drawable.airwallex_ic_online_banking, "Online banking", PaymentMethodClassify.PPRO);

    internal companion object {
        internal fun fromValue(value: String?): PaymentMethodType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
