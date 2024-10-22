package com.airwallex.android.core

import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.NextAction

/**
 * Provide some internal plugins
 */
object AirwallexPlugins {

    const val AIRWALLEX_USER_AGENT = "Airwallex-Android-SDK"

    private var configuration: AirwallexConfiguration =
        AirwallexConfiguration(false, Environment.PRODUCTION, emptyList(), true)

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

    internal val enableAnalytics: Boolean
        get() {
            return configuration.enableAnalytics
        }

    @Suppress("SwallowedException")
    fun getProvider(paymentMethodType: AvailablePaymentMethodType): ActionComponentProvider<out ActionComponent>? {
        return runCatching {
            getProvider(ActionComponentProviderType.valueOf(paymentMethodType.name.uppercase()))
        }.getOrElse {
            if (paymentMethodType.resources?.hasSchema == true) {
                getProvider(ActionComponentProviderType.REDIRECT)
            } else {
                null
            }
        }
    }

    fun getProvider(nextAction: NextAction?): ActionComponentProvider<out ActionComponent>? {
        return configuration.supportComponentProviders.firstOrNull {
            it.canHandleAction(nextAction)
        }
    }

    fun getProvider(type: ActionComponentProviderType): ActionComponentProvider<out ActionComponent>? {
        return configuration.supportComponentProviders.firstOrNull {
            it.getType() == type
        }
    }
}
