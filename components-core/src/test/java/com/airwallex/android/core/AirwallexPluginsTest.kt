package com.airwallex.android.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.AvailablePaymentMethodTypeResource
import com.airwallex.android.core.model.NextAction
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AirwallexPluginsTest {

    private lateinit var application: Application

    @Before
    fun setUp() {
        // Mock Application instance
        application = mockk()
    }

    private class TestComponentProvider(val providerType: ActionComponentProviderType) :
        ActionComponentProvider<TestComponnent> {
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
            fragment: Fragment?,
            activity: Activity,
            applicationContext: Context,
            cardNextActionModel: CardNextActionModel?,
            listener: Airwallex.PaymentResultListener,
            consentId: String?
        ) {
            // no-op
        }

        override fun handleActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?,
            listener: Airwallex.PaymentResultListener?
        ): Boolean {
            return false
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
    fun `test get wechat action component provider`() {
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setSupportComponentProviders(
                    listOf(
                        TestComponentProvider(ActionComponentProviderType.WECHATPAY)
                    )
                )
                .build()
        )
        assertEquals(
            AirwallexPlugins.getProvider(
                AvailablePaymentMethodType(
                    "wechatpay"
                )
            )?.getType(),
            ActionComponentProviderType.WECHATPAY
        )
    }

    @Test
    fun `test get redirect action component provider when hasSchema is true`() {
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
    fun `test get redirect action component provider when hasSchema is false`() {
        AirwallexPlugins.initialize(
            AirwallexConfiguration.Builder()
                .setSupportComponentProviders(
                    listOf(
                        TestComponentProvider(ActionComponentProviderType.REDIRECT)
                    )
                )
                .build()
        )
        assertNull(
            AirwallexPlugins.getProvider(
                AvailablePaymentMethodType(
                    name = "alipaycn",
                    resources = AvailablePaymentMethodTypeResource(false)
                )
            )?.getType()
        )
    }

    @Test
    fun `test can't get action component provider when no providers are registered`() {
        val provider = AirwallexPlugins.getProvider(
            AvailablePaymentMethodType(
                "card"
            )
        )
        assertNull(provider)
    }
}
