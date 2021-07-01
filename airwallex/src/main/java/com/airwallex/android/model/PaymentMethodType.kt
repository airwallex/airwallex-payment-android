package com.airwallex.android.model

import android.os.Parcelable
import com.airwallex.android.R
import kotlinx.parcelize.Parcelize

/**
 * The type of [PaymentMethod]
 */
@Parcelize
enum class PaymentMethodType(
    val value: String,
    val drawableRes: Int = 0,
    val displayName: String = "",
    val classify: PaymentMethodClassify,
    val requiredFields: List<PaymentMethodRequiredField> = emptyList()
) : Parcelable {

    CARD(
        "card",
        0,
        "",
        PaymentMethodClassify.CARD
    ),
    WECHAT(
        "wechatpay",
        R.drawable.airwallex_ic_wechat,
        "WeChat Pay",
        PaymentMethodClassify.WECHAT
    ),

    // Redirect_method
    // * ALIPAY *
    ALIPAY_CN(
        "alipaycn",
        R.drawable.airwallex_ic_alipay_cn,
        "Alipay",
        PaymentMethodClassify.REDIRECT
    ),
    ALIPAY_HK(
        "alipayhk",
        R.drawable.airwallex_ic_alipay_hk,
        "AlipayHK",
        PaymentMethodClassify.REDIRECT
    ),
    TRUE_MONEY(
        "truemoney",
        R.drawable.airwallex_ic_truemoney,
        "Truemoney",
        PaymentMethodClassify.REDIRECT
    ),
    BKASH(
        "bkash",
        R.drawable.airwallex_ic_bkash,
        "bKash",
        PaymentMethodClassify.REDIRECT
    ),
    GCASH(
        "gcash",
        R.drawable.airwallex_ic_gcash,
        "GCash",
        PaymentMethodClassify.REDIRECT
    ),
    DANA(
        "dana",
        R.drawable.airwallex_ic_dana,
        "DANA",
        PaymentMethodClassify.REDIRECT
    ),
    KAKAOPAY(
        "kakaopay",
        R.drawable.airwallex_ic_kakao_pay,
        "Kakao Pay",
        PaymentMethodClassify.REDIRECT
    ),
    TNG(
        "tng",
        R.drawable.airwallex_ic_touchngo,
        "Touch 'n Go",
        PaymentMethodClassify.REDIRECT
    ),

    // * PPRO *
    // PHP
    DRAGON_PAY(
        "dragonpay",
        0,
        "DragonPay",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    // IDR
    PERMATANET(
        "permatanet",
        R.drawable.airwallex_ic_permatanet,
        "Permatanet",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    ALFAMART(
        "alfamart",
        R.drawable.airwallex_ic_alfamart,
        "Alfamart",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    INDOMARET(
        "indomaret",
        R.drawable.airwallex_ic_indomaret,
        "Indomaret",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    DOKU_WALLET(
        "doku_ewallet",
        R.drawable.airwallex_ic_doku_wallet,
        "DOKU Wallet",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    // MYR
    FPX(
        "fpx",
        R.drawable.airwallex_ic_fpx,
        "FPX",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    SEVEN_ELEVEN(
        "seven_eleven",
        R.drawable.airwallex_ic_eleven,
        "7eleven",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    // AUD
    POLI(
        "poli",
        R.drawable.airwallex_ic_poli,
        "POLi",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME
        )
    ),

    // THB
    TESCO_LOTUS(
        "tesco_lotus",
        R.drawable.airwallex_ic_tesco_lotus_cash,
        "Tesco Lotus",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    // SGD
    E_NETS(
        "enets",
        R.drawable.airwallex_ic_enets,
        "eNETS",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    GRAB_PAY(
        "grabpay",
        R.drawable.airwallex_ic_grabpay,
        "Grabpay",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME
        )
    ),

    // JPY
    PAY_EASY(
        "payeasy",
        R.drawable.airwallex_ic_pay_easy,
        "Pay-easy",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    KONBINI(
        "konbini",
        R.drawable.airwallex_ic_konbini,
        "Konbini",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    // GBP
    SKRILL(
        "skrill",
        R.drawable.airwallex_ic_skrill,
        "Skrill",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    // IDR, THB
    BANK_TRANSFER(
        "bank_transfer",
        R.drawable.airwallex_ic_bank_transfer,
        "Bank transfer",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    // THB
    ONLINE_BANKING(
        "online_banking",
        R.drawable.airwallex_ic_online_banking,
        "Online banking",
        PaymentMethodClassify.REDIRECT,
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    );

    internal companion object {
        internal fun fromValue(value: String?): PaymentMethodType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
