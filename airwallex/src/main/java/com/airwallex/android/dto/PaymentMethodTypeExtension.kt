package com.airwallex.android.dto

import androidx.annotation.DrawableRes
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.R

@get:DrawableRes
val PaymentMethodType.drawableRes: Int
    get() {
        return when (this) {
            PaymentMethodType.CARD -> 0
            PaymentMethodType.WECHAT -> R.drawable.airwallex_ic_wechat
            PaymentMethodType.ALIPAY_CN -> R.drawable.airwallex_ic_alipay_cn
            PaymentMethodType.ALIPAY_HK -> R.drawable.airwallex_ic_alipay_hk
            PaymentMethodType.TRUE_MONEY -> R.drawable.airwallex_ic_truemoney
            PaymentMethodType.BKASH -> R.drawable.airwallex_ic_bkash
            PaymentMethodType.GCASH -> R.drawable.airwallex_ic_gcash
            PaymentMethodType.DANA -> R.drawable.airwallex_ic_dana
            PaymentMethodType.KAKAOPAY -> R.drawable.airwallex_ic_kakao_pay
            PaymentMethodType.TNG -> R.drawable.airwallex_ic_touchngo
            PaymentMethodType.DRAGON_PAY -> 0
            PaymentMethodType.PERMATANET -> R.drawable.airwallex_ic_permatanet
            PaymentMethodType.ALFAMART -> R.drawable.airwallex_ic_alfamart
            PaymentMethodType.INDOMARET -> R.drawable.airwallex_ic_indomaret
            PaymentMethodType.DOKU_WALLET -> R.drawable.airwallex_ic_doku_wallet
            PaymentMethodType.FPX -> R.drawable.airwallex_ic_fpx
            PaymentMethodType.SEVEN_ELEVEN -> R.drawable.airwallex_ic_eleven
            PaymentMethodType.POLI -> R.drawable.airwallex_ic_poli
            PaymentMethodType.TESCO_LOTUS -> R.drawable.airwallex_ic_tesco_lotus_cash
            PaymentMethodType.E_NETS -> R.drawable.airwallex_ic_enets
            PaymentMethodType.GRAB_PAY -> R.drawable.airwallex_ic_grabpay
            PaymentMethodType.PAY_EASY -> R.drawable.airwallex_ic_pay_easy
            PaymentMethodType.KONBINI -> R.drawable.airwallex_ic_konbini
            PaymentMethodType.SKRILL -> R.drawable.airwallex_ic_skrill
            PaymentMethodType.BANK_TRANSFER -> R.drawable.airwallex_ic_bank_transfer
            PaymentMethodType.ONLINE_BANKING -> R.drawable.airwallex_ic_online_banking
        }
    }
