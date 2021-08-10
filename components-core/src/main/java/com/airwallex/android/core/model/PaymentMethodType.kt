package com.airwallex.android.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The type of [PaymentMethod]
 */
@Parcelize
enum class PaymentMethodType(
    val value: String,
    val displayName: String = "",
    val requiredFields: List<PaymentMethodRequiredField> = emptyList()
) : Parcelable {

    CARD(
        "card",
        ""
    ),
    WECHAT(
        "wechatpay",
        "WeChat Pay"
    ),

    // Redirect_method
    // * ALIPAY *
    ALIPAY_CN(
        "alipaycn",
        "Alipay"
    ),
    ALIPAY_HK(
        "alipayhk",
        "AlipayHK"
    ),
    TRUE_MONEY(
        "truemoney",
        "TrueMoney"
    ),
    BKASH(
        "bkash",
        "bKash"
    ),
    GCASH(
        "gcash",
        "GCash"
    ),
    DANA(
        "dana",
        "DANA"
    ),
    KAKAOPAY(
        "kakaopay",
        "Kakao Pay"
    ),
    TNG(
        "tng",
        "Touch 'n Go"
    ),

    // * PPRO *
    // PHP
    DRAGON_PAY(
        "dragonpay",
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
        "Permatanet",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    ALFAMART(
        "alfamart",
        "Alfamart",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    INDOMARET(
        "indomaret",
        "Indomaret",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    DOKU_WALLET(
        "doku_ewallet",
        "DOKU Wallet",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    // MYR
    FPX(
        "fpx",
        "FPX",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    SEVEN_ELEVEN(
        "seven_eleven",
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
        "POLi",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME
        )
    ),

    // THB
    TESCO_LOTUS(
        "tesco_lotus",
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
        "eNETS",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    GRAB_PAY(
        "grabpay",
        "Grabpay",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME
        )
    ),

    // JPY
    PAY_EASY(
        "payeasy",
        "Pay-easy",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    ),

    KONBINI(
        "konbini",
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
        "Skrill",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL
        )
    ),

    // IDR, THB
    BANK_TRANSFER(
        "bank_transfer",
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
        "Online banking",
        listOf(
            PaymentMethodRequiredField.SHOPPER_NAME,
            PaymentMethodRequiredField.SHOPPER_EMAIL,
            PaymentMethodRequiredField.SHOPPER_PHONE
        )
    );

    companion object {
        fun fromValue(value: String?): PaymentMethodType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
