package com.airwallex.android.model

import android.util.Log
import com.airwallex.android.ParcelUtils
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoginTest {

    @Test
    fun builderConstructor() {
        val loginInfo = LoginInfo.Builder()
            .setApiKey("3fb8c8095d2ecee1451d62d0abfe74a21a8e3324afed60dc5e2ca27370f3b9a540716bb7078fdc5b020581d5e97fbafc")
            .setClientId("vvXOivbXQt-bjXxFicgLgA")
            .setAppId("wxfad13fd6681a62b0")
            .build()
        Log.i("loginInfo","${loginInfo}")
        assertEquals(loginInfo, LoginFixtures.loginInfo)
    }

    @Test
    fun testParcelable() {
        assertEquals(LoginFixtures.loginInfo, ParcelUtils.create(LoginFixtures.loginInfo))
    }
}
