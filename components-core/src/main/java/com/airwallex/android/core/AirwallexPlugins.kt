package com.airwallex.android.core

import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction

/**
 * Provide some internal plugins
 */
object AirwallexPlugins {

    const val AIRWALLEX_USER_AGENT = "Airwallex-Android-SDK"

    private var configuration: AirwallexConfiguration =
        AirwallexConfiguration(false, Environment.PRODUCTION, emptyList())

    fun initialize(configuration: AirwallexConfiguration) {
        this.configuration = configuration
    }

    /**
     * Enable logging in the Airwallex
     */
    internal val enableLogging: Boolean
        get() {
            return configuration.enableLogging
        }

    /**
     * Environment in the Airwallex
     */
    val environment: Environment
        get() {
            return configuration.environment
        }

    @Suppress("SwallowedException")
    fun getProvider(paymentMethodType: AvailablePaymentMethodType): ActionComponentProvider<out ActionComponent>? {
        return try {
            getProvider(ActionComponentProviderType.valueOf(paymentMethodType.name.uppercase()))
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun getProvider(nextAction: NextAction?): ActionComponentProvider<out ActionComponent>? {
        return configuration.supportComponentProviders.firstOrNull {
            it.canHandleAction(nextAction)
        }
    }

    fun getCardProvider(): ActionComponentProvider<out ActionComponent>? {
        return configuration.supportComponentProviders.firstOrNull {
            it.getType() == ActionComponentProviderType.CARD
        }
    }

    private fun getProvider(type: ActionComponentProviderType): ActionComponentProvider<out ActionComponent>? {
        return configuration.supportComponentProviders.firstOrNull {
            it.getType() == type
        }
    }
}
