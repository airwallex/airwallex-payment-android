package com.airwallex.android.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.AvailablePaymentMethodTypeResource
import com.airwallex.android.core.model.NextAction
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AirwallexPluginsTest {
    private class TestComponentProvider(val providerType: ActionComponentProviderType) : ActionComponentProvider<TestComponnent> {
        override fun get(): TestComponnent {
            return TestComponnent()
        }

        override fun getType(): ActionComponentProviderType {
            return providerType
        }

        override fun canHandleAction(nextAction: NextAction?): Boolean {
            return false
        }
    }

    private class TestComponnent : ActionComponent {
        override fun handlePaymentIntentResponse(
            paymentIntentId: String,
            nextAction: NextAction?,
            activity: Activity,
            applicationContext: Context,
            cardNextActionModel: CardNextActionModel?,
            listener: Airwallex.PaymentResultListener
        ) {
            // no-op
        }

        override fun handleActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ): Boolean {
            return false
        }

        override fun retrieveSecurityToken(
            paymentIntentId: String,
            applicationContext: Context,
            securityTokenListener: SecurityTokenListener
        ) {
            // no-op
        }
    }

    @Test
    fun `test REST client`() {
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .enableLogging(true)
                .setEnvironment(Environment.DEMO)
                .build()
        )
        assertEquals(true, AirwallexPlugins.enableLogging)
        assertEquals(Environment.DEMO, AirwallexPlugins.environment)
    }

    @Test
    fun `test get Google Pay action component provider`() {
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setSupportComponentProviders(
                    listOf(
                        TestComponentProvider(ActionComponentProviderType.GOOGLEPAY)
                    )
                )
                .build()
        )
        assertEquals(
            AirwallexPlugins.getProvider(
                AvailablePaymentMethodType(
                    "googlepay"
                )
            )?.getType(),
            ActionComponentProviderType.GOOGLEPAY
        )
    }

    @Test
    fun `test get redirect action component provider`() {
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setSupportComponentProviders(
                    listOf(
                        TestComponentProvider(ActionComponentProviderType.REDIRECT)
                    )
                )
                .build()
        )
        assertEquals(
            AirwallexPlugins.getProvider(
                AvailablePaymentMethodType(
                    name = "alipaycn",
                    resources = AvailablePaymentMethodTypeResource(true)
                )
            )?.getType(),
            ActionComponentProviderType.REDIRECT
        )
    }

    @Test
    fun `test can't get action component provider`() {
        val provider = AirwallexPlugins.getProvider(
            AvailablePaymentMethodType(
                "card"
            )
        )
        assertNull(provider)
    }
}
