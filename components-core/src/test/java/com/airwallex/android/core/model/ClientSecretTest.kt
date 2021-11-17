package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ClientSecretTest {

    @Test
    fun testParcelable() {
        assertEquals(
            ClientSecretFixtures.CLIENTSECRET,
            ParcelUtils.create(ClientSecretFixtures.CLIENTSECRET)
        )
    }

    @Test
    fun testParams() {
        val clientSecret = ClientSecretFixtures.CLIENTSECRET
        assertEquals("vvXOivbXQt-bjXxFicgLgA", clientSecret.value)

        val calendar = Calendar.getInstance()
        calendar.time = clientSecret.expiredTime
        assertEquals(2015, calendar.get(Calendar.YEAR))
    }
}
