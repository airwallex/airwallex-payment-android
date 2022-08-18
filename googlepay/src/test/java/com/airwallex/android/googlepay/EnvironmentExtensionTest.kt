package com.airwallex.android.googlepay

import com.airwallex.android.core.Environment
import com.google.android.gms.wallet.WalletConstants
import org.junit.Assert.assertEquals
import org.junit.Test

class EnvironmentExtensionTest {
    @Test
    fun `test googlePayEnvironment if demo`() {
        val environment = Environment.DEMO
        assertEquals(environment.googlePayEnvironment(), WalletConstants.ENVIRONMENT_TEST)
    }

    @Test
    fun `test googlePayEnvironment if staging`() {
        val environment = Environment.STAGING
        assertEquals(environment.googlePayEnvironment(), WalletConstants.ENVIRONMENT_TEST)
    }

    @Test
    fun `test googlePayEnvironment if production`() {
        val environment = Environment.PRODUCTION
        assertEquals(environment.googlePayEnvironment(), WalletConstants.ENVIRONMENT_PRODUCTION)
    }
}