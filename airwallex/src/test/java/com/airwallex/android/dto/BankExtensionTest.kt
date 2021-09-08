package com.airwallex.android.dto

import com.airwallex.android.core.model.Bank
import com.airwallex.android.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class BankExtensionTest {

    @Test
    fun drawableResTest() {
        assertEquals(Bank.BANGKOK.drawableRes, R.drawable.airwallex_ic_bangkok_bank)
        assertEquals(Bank.KRUNGSRI.drawableRes, R.drawable.airwallex_ic_krungsri_bank)
        assertEquals(Bank.KRUNG_THAI.drawableRes, R.drawable.airwallex_ic_krung_thai_bank)
        assertEquals(Bank.SIAM_COMMERCIAL.drawableRes, R.drawable.airwallex_ic_siam_commercial_bank)
        assertEquals(Bank.BANK_MANDIRI.drawableRes, R.drawable.airwallex_ic_bank_mandiri)
        assertEquals(Bank.BANK_DANAMON.drawableRes, R.drawable.airwallex_ic_bank_danamon)
        assertEquals(Bank.CIMB_NIAGA.drawableRes, R.drawable.airwallex_ic_cimb_bank)
        assertEquals(Bank.PERMATA.drawableRes, R.drawable.airwallex_ic_permata)
        assertEquals(Bank.MAY_BANK.drawableRes, R.drawable.airwallex_ic_maybank)
    }
}
