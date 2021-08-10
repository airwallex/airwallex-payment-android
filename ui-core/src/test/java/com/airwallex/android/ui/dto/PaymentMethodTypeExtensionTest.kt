package com.airwallex.android.ui.dto

import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.ui.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PaymentMethodTypeExtensionTest {

    @Test
    fun drawableResTest() {
        assertEquals(PaymentMethodType.CARD.drawableRes, 0)
        assertEquals(PaymentMethodType.WECHAT.drawableRes, R.drawable.airwallex_ic_wechat)
        assertEquals(PaymentMethodType.ALIPAY_CN.drawableRes, R.drawable.airwallex_ic_alipay_cn)
        assertEquals(PaymentMethodType.ALIPAY_HK.drawableRes, R.drawable.airwallex_ic_alipay_hk)
        assertEquals(PaymentMethodType.TRUE_MONEY.drawableRes, R.drawable.airwallex_ic_truemoney)
        assertEquals(PaymentMethodType.BKASH.drawableRes, R.drawable.airwallex_ic_bkash)
        assertEquals(PaymentMethodType.GCASH.drawableRes, R.drawable.airwallex_ic_gcash)
        assertEquals(PaymentMethodType.DANA.drawableRes, R.drawable.airwallex_ic_dana)
        assertEquals(PaymentMethodType.KAKAOPAY.drawableRes, R.drawable.airwallex_ic_kakao_pay)
        assertEquals(PaymentMethodType.TNG.drawableRes, R.drawable.airwallex_ic_touchngo)
        assertEquals(PaymentMethodType.DRAGON_PAY.drawableRes, 0)
        assertEquals(PaymentMethodType.PERMATANET.drawableRes, R.drawable.airwallex_ic_permatanet)
        assertEquals(PaymentMethodType.ALFAMART.drawableRes, R.drawable.airwallex_ic_alfamart)
        assertEquals(PaymentMethodType.INDOMARET.drawableRes, R.drawable.airwallex_ic_indomaret)
        assertEquals(PaymentMethodType.DOKU_WALLET.drawableRes, R.drawable.airwallex_ic_doku_wallet)
        assertEquals(PaymentMethodType.FPX.drawableRes, R.drawable.airwallex_ic_fpx)
        assertEquals(PaymentMethodType.SEVEN_ELEVEN.drawableRes, R.drawable.airwallex_ic_eleven)
        assertEquals(PaymentMethodType.POLI.drawableRes, R.drawable.airwallex_ic_poli)
        assertEquals(PaymentMethodType.TESCO_LOTUS.drawableRes, R.drawable.airwallex_ic_tesco_lotus_cash)
        assertEquals(PaymentMethodType.E_NETS.drawableRes, R.drawable.airwallex_ic_enets)
        assertEquals(PaymentMethodType.GRAB_PAY.drawableRes, R.drawable.airwallex_ic_grabpay)
        assertEquals(PaymentMethodType.PAY_EASY.drawableRes, R.drawable.airwallex_ic_pay_easy)
        assertEquals(PaymentMethodType.KONBINI.drawableRes, R.drawable.airwallex_ic_konbini)
        assertEquals(PaymentMethodType.SKRILL.drawableRes, R.drawable.airwallex_ic_skrill)
        assertEquals(PaymentMethodType.BANK_TRANSFER.drawableRes, R.drawable.airwallex_ic_bank_transfer)
        assertEquals(PaymentMethodType.ONLINE_BANKING.drawableRes, R.drawable.airwallex_ic_online_banking)
    }
}
