package com.airwallex.android.view

import com.airwallex.android.view.util.CountryUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class CountryUtilsTest {

    @Test
    fun countryByNameTest() {
        assertEquals("CN", CountryUtils.getCountryByName("China")?.code)
    }

    @Test
    fun countryByCodeTest() {
        assertEquals("China", CountryUtils.getCountryByCode("CN")?.name)
    }
}
