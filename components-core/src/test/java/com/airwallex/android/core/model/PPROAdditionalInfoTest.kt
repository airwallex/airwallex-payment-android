package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class PPROAdditionalInfoTest {

    val ppro = PPROAdditionalInfo(
        name = "111",
        email = "aaa@cc.vv",
        phone = "12345",
        bank = Bank.MAY_BANK
    )

    @Test
    fun testParams() {
        assertEquals("111", ppro.name)
        assertEquals("aaa@cc.vv", ppro.email)
        assertEquals("12345", ppro.phone)
        assertEquals(Bank.MAY_BANK, ppro.bank)
    }
}
