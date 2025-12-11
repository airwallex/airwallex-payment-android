package com.airwallex.android

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexConfiguration
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.AirwallexShippingStatus
import com.airwallex.android.core.AirwallexSupportedCard
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.model.CardScheme
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.ui.AirwallexActivityLaunch
import com.airwallex.android.view.AddPaymentMethodActivityLaunch
import com.airwallex.android.view.PaymentMethodsActivityLaunch
import com.airwallex.android.view.PaymentShippingActivityLaunch
import com.airwallex.android.core.util.SessionUtils.getIntentId
import com.airwallex.risk.AirwallexRisk

/**
 *  Entry-point to the Airwallex Payment Flow. Create a AirwallexStarter attached to the given host Activity.
 */
class AirwallexStarter {

    companion object {
        /**
         * Initialize some global configurations, better to be called on Application
         */
        fun initialize(
            application: Application,
            configuration: AirwallexConfiguration,
        ) {
            initializeActivityLaunch(application)
            Airwallex.initialize(application, configuration)
        }

        /**
         * Initialize Airwallex Launch, if you have invoked [initialize] before, no need to call this method
         */
        fun initializeActivityLaunch(application: Application) {
            AirwallexActivityLaunch.initialize(application)
        }

        private fun setupAnalyticsLogger(session: AirwallexSession) {
            when (session) {
                is AirwallexPaymentSession -> {
                    AnalyticsLogger.setSessionInformation(
                        transactionMode = TransactionMode.ONE_OFF.value,
                        paymentIntentId = session.paymentIntent?.id,
                    )
                }
                is AirwallexRecurringSession -> {
                    AnalyticsLogger.setSessionInformation(
                        transactionMode = TransactionMode.RECURRING.value,
                    )
                }
                is AirwallexRecurringWithIntentSession -> {
                    AnalyticsLogger.setSessionInformation(
                        transactionMode = TransactionMode.RECURRING.value,
                        paymentIntentId = session.paymentIntent?.id,
                    )
                }
            }
        }

        /**
         * Launch the card payment flow to allow the user to complete the entire payment flow
         *
         * @param activity the launch activity on which the payment UI is presented
         * @param session a [AirwallexSession] used to present the payment flow
         * @param paymentResultListener The callback of present entire payment flow
         */
        fun presentCardPaymentFlow(
            activity: ComponentActivity,
            session: AirwallexSession,
            supportedCards: List<AirwallexSupportedCard> = enumValues<AirwallexSupportedCard>().toList(),
            paymentResultListener: Airwallex.PaymentResultListener,
        ) {
            setupAnalyticsLogger(session)
            AirwallexRisk.log(AirwallexRisk.Events.TRANSACTION_INITIATED)
            val intentId = getIntentId(session)
            AirwallexLogger.info("AirwallexStarter presentCardPaymentFlow[$intentId]")
            AddPaymentMethodActivityLaunch(activity)
                .launchForResult(
                    AddPaymentMethodActivityLaunch.Args.Builder()
                        .setAirwallexSession(session)
                        .setSupportedCardSchemes(supportedCards.map { CardScheme(it.brandName) })
                        .build()
                ) { _, result ->
                    handleCardPaymentData(result.resultCode, result.data, paymentResultListener, intentId)
                }
        }

        /**
         * Launch the shipping flow to allow the user to fill the shipping information
         *
         * @param fragment fragment {@link Fragment}
         * @param shipping a [Shipping] used to present the shipping flow, it's optional
         * @param shippingResultListener The callback of present the shipping flow
         */
        fun presentShippingFlow(
            fragment: Fragment,
            shipping: Shipping?,
            shippingResultListener: Airwallex.ShippingResultListener
        ) {
            presentShippingFlow(
                PaymentShippingActivityLaunch(fragment),
                shipping,
                shippingResultListener
            )
        }

        /**
         * Launch the shipping flow to allow the user to fill the shipping information
         *
         * @param activity the launch activity on which the shipping UI is presented
         * @param shipping a [Shipping] used to present the shipping flow, it's optional
         * @param shippingResultListener The callback of present the shipping flow
         */
        fun presentShippingFlow(
            activity: ComponentActivity,
            shipping: Shipping?,
            shippingResultListener: Airwallex.ShippingResultListener
        ) {
            presentShippingFlow(
                PaymentShippingActivityLaunch(activity),
                shipping,
                shippingResultListener
            )
        }

        private fun presentShippingFlow(
            launch: PaymentShippingActivityLaunch,
            shipping: Shipping?,
            shippingResultListener: Airwallex.ShippingResultListener
        ) {
            launch.launchForResult(
                PaymentShippingActivityLaunch.Args.Builder()
                    .setShipping(shipping)
                    .build()
            ) { _, result ->
                handleShippingPaymentData(result.resultCode, result.data, shippingResultListener)
            }
        }

        /**
         * Launch the payment flow to allow the user to complete the entire payment flow
         *
         * @param fragment fragment {@link Fragment}
         * @param session a [AirwallexSession] used to present the payment flow
         * @param paymentResultListener The callback of present entire payment flow
         * @param layoutType PaymentMethodsLayoutType for payment methods list UI. Two types are supported: Tab and Accordion.
         */
        @Deprecated(message = "Use presentEntirePaymentFlow() instead")
        fun presentPaymentFlow(
            fragment: Fragment,
            session: AirwallexSession,
            layoutType: PaymentMethodsLayoutType = PaymentMethodsLayoutType.TAB,
            paymentResultListener: Airwallex.PaymentResultListener,
        ) {
            presentPaymentFlow(
                PaymentMethodsActivityLaunch(fragment),
                session,
                layoutType,
                paymentResultListener,
            )
        }

        /**
         * Launch the payment flow to allow the user to complete the entire payment flow
         *
         * @param activity the launch activity on which the payment UI is presented
         * @param session a [AirwallexSession] used to present the payment flow
         * @param paymentResultListener The callback of present entire payment flow
         * @param layoutType PaymentMethodsLayoutType for payment methods list UI. Two types are supported: Tab and Accordion.
         */
        @Deprecated(message = "Use presentEntirePaymentFlow() instead")
        fun presentPaymentFlow(
            activity: ComponentActivity,
            session: AirwallexSession,
            layoutType: PaymentMethodsLayoutType = PaymentMethodsLayoutType.TAB,
            paymentResultListener: Airwallex.PaymentResultListener,
        ) {
            presentPaymentFlow(
                PaymentMethodsActivityLaunch(activity),
                session,
                layoutType,
                paymentResultListener,
            )
        }

        /**
         * Launch the payment flow to allow the user to complete the entire payment flow
         *
         * @param activity the launch activity on which the payment UI is presented
         * @param session a [AirwallexSession] used to present the payment flow
         * @param paymentResultListener The callback of present entire payment flow
         * @param layoutType PaymentMethodsLayoutType for payment methods list UI. Two types are supported: Tab and Accordion.
         */
        fun presentEntirePaymentFlow(
            activity: ComponentActivity,
            session: AirwallexSession,
            layoutType: PaymentMethodsLayoutType = PaymentMethodsLayoutType.TAB,
            paymentResultListener: Airwallex.PaymentResultListener,
        ) {
            presentPaymentFlow(
                PaymentMethodsActivityLaunch(activity),
                session,
                layoutType,
                paymentResultListener,
            )
        }

        private fun presentPaymentFlow(
            launch: PaymentMethodsActivityLaunch,
            session: AirwallexSession,
            layoutType: PaymentMethodsLayoutType,
            paymentResultListener: Airwallex.PaymentResultListener,
        ) {
            setupAnalyticsLogger(session)
            AirwallexRisk.log(AirwallexRisk.Events.TRANSACTION_INITIATED)
            val intentId = getIntentId(session)
            AirwallexLogger.info("AirwallexStarter presentPaymentFlow[$intentId]")
            launch.launchForResult(
                PaymentMethodsActivityLaunch.Args.Builder()
                    .setAirwallexSession(session)
                    .setLayoutType(layoutType)
                    .build()
            ) { _, result ->
                handlePaymentData(
                    result.resultCode,
                    result.data,
                    paymentResultListener,
                    intentId,
                )
            }
        }

        /**
         * Method to handle Activity results from Airwallex activities.
         *
         * @param resultCode a result code representing the success of the intended action
         * @param data an [Intent] with the resulting data from the Activity
         *
         */
        private fun handleShippingPaymentData(
            resultCode: Int,
            data: Intent?,
            shippingResultListener: Airwallex.ShippingResultListener
        ) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val result = PaymentShippingActivityLaunch.Result.fromIntent(data)
                    if (result == null) {
                        AirwallexLogger.error("AirwallexStarter handleShippingPaymentData: failed, result = null")
                        shippingResultListener.onCompleted(
                            AirwallexShippingStatus.Failure(AirwallexCheckoutException(message = "shipping result is null"))
                        )
                        return
                    }
                    AirwallexLogger.info("AirwallexStarter handleShippingPaymentData: success")
                    shippingResultListener.onCompleted(
                        AirwallexShippingStatus.Success(result.shipping)
                    )
                }

                Activity.RESULT_CANCELED -> {
                    AirwallexLogger.info("AirwallexStarter handleShippingPaymentData: cancel")
                    shippingResultListener.onCompleted(AirwallexShippingStatus.Cancel)
                }

            }
        }

        /**
         * Method to handle Activity results from Airwallex activities.
         *
         * @param resultCode a result code representing the success of the intended action
         * @param data an [Intent] with the resulting data from the Activity
         *
         */
        private fun handlePaymentData(
            resultCode: Int,
            data: Intent?,
            paymentResultListener: Airwallex.PaymentResultListener,
            intentId: String
        ) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val result = PaymentMethodsActivityLaunch.Result.fromIntent(data)
                    if (result == null) {
                        AirwallexLogger.error("AirwallexStarter handlePaymentData[$intentId]: failed, result = null")
                        paymentResultListener.onCompleted(
                            AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "flow result is null"))
                        )
                        return
                    }
                    when {
                        result.exception != null -> {
                            AirwallexLogger.error(
                                "AirwallexStarter handlePaymentData[$intentId]: failed",
                                result.exception
                            )
                            paymentResultListener.onCompleted(
                                AirwallexPaymentStatus.Failure(result.exception)
                            )
                        }

                        result.paymentIntentId != null -> {
                            AirwallexLogger.info("AirwallexStarter handlePaymentData[$intentId]: success, isRedirecting = ${result.isRedirecting}")
                            if (result.isRedirecting) {
                                paymentResultListener.onCompleted(
                                    AirwallexPaymentStatus.InProgress(result.paymentIntentId)
                                )
                            } else {
                                paymentResultListener.onCompleted(
                                    AirwallexPaymentStatus.Success(
                                        result.paymentIntentId,
                                        result.paymentConsentId
                                    )
                                )
                            }
                        }
                    }
                }

                Activity.RESULT_CANCELED -> {
                    AirwallexLogger.info("AirwallexStarter handlePaymentData[$intentId]: cancel")
                    paymentResultListener.onCompleted(AirwallexPaymentStatus.Cancel)
                }
            }
        }

        /**
         * Method to handle Activity results from Airwallex activities.
         *
         * @param resultCode a result code representing the success of the intended action
         * @param data an [Intent] with the resulting data from the Activity
         *
         */
        private fun handleCardPaymentData(
            resultCode: Int,
            data: Intent?,
            paymentResultListener: Airwallex.PaymentResultListener,
            intentId: String
        ) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val result = AddPaymentMethodActivityLaunch.Result.fromIntent(data)
                    if (result == null) {
                        AirwallexLogger.error("AirwallexStarter handleCardPaymentData[$intentId]: failed, result = null")
                        paymentResultListener.onCompleted(
                            AirwallexPaymentStatus.Failure(AirwallexCheckoutException(message = "flow result is null"))
                        )
                        return
                    }
                    when {
                        result.exception != null -> {
                            AirwallexLogger.error(
                                "AirwallexStarter handleCardPaymentData[$intentId]: failed",
                                result.exception
                            )
                            paymentResultListener.onCompleted(
                                AirwallexPaymentStatus.Failure(result.exception)
                            )
                        }

                        result.paymentIntentId != null -> {
                            AirwallexLogger.info("AirwallexStarter handleCardPaymentData[$intentId]: success,}")
                            paymentResultListener.onCompleted(
                                AirwallexPaymentStatus.Success(
                                    result.paymentIntentId,
                                    result.consentId
                                )
                            )
                        }
                    }
                }

                Activity.RESULT_CANCELED -> {
                    AirwallexLogger.info("AirwallexStarter handleCardPaymentData[$intentId]: cancel")
                    paymentResultListener.onCompleted(AirwallexPaymentStatus.Cancel)
                }
            }
        }
    }
}
