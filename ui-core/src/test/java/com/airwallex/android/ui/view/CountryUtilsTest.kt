package com.airwallex.android.ui.view

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
