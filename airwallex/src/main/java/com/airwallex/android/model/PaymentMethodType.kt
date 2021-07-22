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
    val requiredFields: List<PaymentMethodRequiredField> = emptyList()
) : Parcelable {

    CARD(
        "card",
        0,
        ""
    ),
    WECHAT(
        "wechatpay",
        R.drawable.airwallex_ic_wechat,
        "WeChat Pay"
    ),

    // Redirect_method
    // * ALIPAY *
    ALIPAY_CN(
        "alipaycn",
        R.drawable.airwallex_ic_alipay_cn,
        "Alipay"
    ),
    ALIPAY_HK(
        "alipayhk",
        R.drawable.airwallex_ic_alipay_hk,
        "AlipayHK"
    ),
    TRUE_MONEY(
        "truemoney",
        R.drawable.airwallex_ic_truemoney,
        "TrueMoney"
    ),
    BKASH(
        "bkash",
        R.drawable.airwallex_ic_bkash,
        "bKash"
    ),
    GCASH(
        "gcash",
        R.drawable.airwallex_ic_gcash,
        "GCash"
    ),
    DANA(
        "dana",
        R.drawable.airwallex_ic_dana,
        "DANA"
    ),
    KAKAOPAY(
        "kakaopay",
        R.drawable.airwallex_ic_kakao_pay,
        "Kakao Pay"
    ),
    TNG(
        "tng",
        R.drawable.airwallex_ic_touchngo,
        "Touch 'n Go"
    ),

    // * PPRO *
    // PHP
    DRAGON_PAY(
        "dragonpay",
        0,
        "DragonPay",
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
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    ALFAMART(
        "alfamart",
        R.drawable.airwallex_ic_alfamart,
        "Alfamart",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    INDOMARET(
        "indomaret",
        R.drawable.airwallex_ic_indomaret,
        "Indomaret",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    DOKU_WALLET(
        "doku_ewallet",
        R.drawable.airwallex_ic_doku_wallet,
        "DOKU Wallet",
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
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME
        )
    ),

    // THB
    TESCO_LOTUS(
        "tesco_lotus",
        R.drawable.airwallex_ic_tesco_lotus_cash,
        "Tesco Lotus",
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
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME
        )
    ),

    // JPY
    PAY_EASY(
        "payeasy",
        R.drawable.airwallex_ic_pay_easy,
        "Pay-easy",
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
