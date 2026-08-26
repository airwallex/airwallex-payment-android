package com.airwallex.android.core

import androidx.activity.ComponentActivity
import com.airwallex.android.core.exception.AirwallexCheckoutException
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.log.AirwallexLogger
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.Options
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.RetrieveAvailablePaymentConsentsParams
import com.airwallex.android.core.model.RetrieveAvailablePaymentMethodParams
import com.airwallex.android.core.model.TransactionMode
import com.airwallex.android.core.model.withMaestroIfMasterCard
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import java.util.Collections
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/**
 * Retrieves and filters the payment methods and consents available for a session against the
 * Airwallex backend. Backs the public retrieval APIs exposed by [Airwallex].
 */
internal interface AvailablePaymentMethodsService {

    suspend fun retrieveAvailablePaymentConsents(
        params: RetrieveAvailablePaymentConsentsParams
    ): Page<PaymentConsent>

    suspend fun retrieveAvailablePaymentMethods(
        session: AirwallexSession,
        params: RetrieveAvailablePaymentMethodParams
    ): Page<AvailablePaymentMethodType>

    suspend fun fetchAvailablePaymentMethodsAndConsents(
        session: AirwallexSession
    ): Result<Pair<List<AvailablePaymentMethodType>, List<PaymentConsent>>>
}

internal class DefaultAvailablePaymentMethodsService(
    private val paymentManager: PaymentManager,
    private val activityProvider: () -> ComponentActivity,
    private val setupAnalyticsLogger: (AirwallexSession) -> Unit,
    private val resolveLanguageCode: (Locale?) -> String,
) : AvailablePaymentMethodsService {

    override suspend fun retrieveAvailablePaymentConsents(
        params: RetrieveAvailablePaymentConsentsParams
    ): Page<PaymentConsent> {
        return paymentManager.retrieveAvailablePaymentConsents(
            Options.RetrieveAvailablePaymentConsentsOptions(
                clientSecret = params.clientSecret,
                customerId = params.customerId,
                merchantTriggerReason = params.merchantTriggerReason,
                nextTriggeredBy = params.nextTriggeredBy,
                status = params.status,
                pageNum = params.pageNum,
                pageSize = params.pageSize
            )
        )
    }

    override suspend fun retrieveAvailablePaymentMethods(
        session: AirwallexSession,
        params: RetrieveAvailablePaymentMethodParams
    ): Page<AvailablePaymentMethodType> {
        setupAnalyticsLogger(session)
        val transactionMode = when (session) {
            is Session -> if (session.isOneOffPayment) TransactionMode.ONE_OFF else TransactionMode.RECURRING
            is AirwallexRecurringSession, is AirwallexRecurringWithIntentSession -> TransactionMode.RECURRING
            is AirwallexPaymentSession -> TransactionMode.ONE_OFF
            else -> throw AirwallexCheckoutException(message = "Not support session $session")
        }
        AirwallexLogger.info("Airwallex retrieveAvailablePaymentMethods[${(session as? AirwallexPaymentSession)?.paymentIntent?.id}]: transactionMode = $transactionMode ")
        val response = paymentManager.retrieveAvailablePaymentMethods(
            Options.RetrieveAvailablePaymentMethodsOptions(
                clientSecret = params.clientSecret,
                pageNum = params.pageNum,
                pageSize = params.pageSize,
                active = params.active,
                transactionCurrency = params.transactionCurrency,
                transactionMode = transactionMode,
                countryCode = params.countryCode,
                languageCode = resolveLanguageCode(session.locale)
            )
        )
        response.items = response.items.filter { paymentMethod ->
            paymentMethod.transactionMode == transactionMode &&
                    AirwallexPlugins.getProvider(paymentMethod)?.canHandleSessionAndPaymentMethod(
                        session,
                        paymentMethod,
                        activityProvider()
                    ) ?: false
        }
        AirwallexLogger.info("Airwallex retrieveAvailablePaymentMethods[${(session as? AirwallexPaymentSession)?.paymentIntent?.id}]: response.items.size = ${response.items.size}")
        return response
    }

    override suspend fun fetchAvailablePaymentMethodsAndConsents(
        session: AirwallexSession
    ): Result<Pair<List<AvailablePaymentMethodType>, List<PaymentConsent>>> {
        val secret =
            session.clientSecret?.takeIf { it.isNotBlank() } ?: return Result.failure(
                AirwallexCheckoutException(message = "Client secret is empty or blank")
            )
        val customerId = session.customerId
        return supervisorScope {
            val intentId = (session as? AirwallexPaymentSession)?.paymentIntent?.id
            AirwallexLogger.info("Airwallex fetchAvailablePaymentMethodsAndConsents$intentId: customerId = $customerId")
            val retrieveConsents = async {
                customerId?.takeIf { needRequestConsent(session) }
                    ?.let { retrieveAvailablePaymentConsentsPaged(secret, it) } ?: emptyList()
            }
            val retrieveMethods = async { retrieveAvailablePaymentMethodsPaged(session, secret) }
            try {
                val methods = addMaestroWhenMasterCardPresent(
                    filterPaymentMethodsBySession(
                        retrieveMethods.await(), session.paymentMethods
                    )
                )
                val consents = retrieveConsents.await()
                Result.success(
                    Pair(
                        methods,
                        filterPaymentConsentsBySession(session, methods, consents)
                    )
                )
            } catch (exception: AirwallexException) {
                AirwallexLogger.error(
                    "Airwallex fetchAvailablePaymentMethodsAndConsents$intentId: failed ",
                    exception
                )
                Result.failure(exception)
            }
        }
    }

    private suspend fun retrieveAvailablePaymentConsentsPaged(
        clientSecret: String,
        customerId: String,
    ) = loadPagedItems(
        loadPage = { pageNum ->
            retrieveAvailablePaymentConsents(
                RetrieveAvailablePaymentConsentsParams.Builder(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    pageNum = pageNum,
                ).setStatus(PaymentConsent.PaymentConsentStatus.VERIFIED).build()
            )
        }
    )

    private suspend fun retrieveAvailablePaymentMethodsPaged(
        session: AirwallexSession,
        clientSecret: String
    ) = loadPagedItems(
        loadPage = { pageNum ->
            retrieveAvailablePaymentMethods(
                session = session,
                params = RetrieveAvailablePaymentMethodParams.Builder(
                    clientSecret = clientSecret,
                    pageNum = pageNum,
                )
                    .setActive(true)
                    .setTransactionCurrency(session.currency)
                    .setCountryCode(session.countryCode).build()
            )
        }
    )

    private fun filterPaymentMethodsBySession(
        sourceList: List<AvailablePaymentMethodType>,
        filterList: List<String>?,
    ): List<AvailablePaymentMethodType> {
        if (filterList.isNullOrEmpty()) return sourceList
        return filterList.mapNotNull { name ->
            sourceList.find { it.name.equals(name, ignoreCase = true) }
        }
    }

    private fun addMaestroWhenMasterCardPresent(
        methods: List<AvailablePaymentMethodType>,
    ): List<AvailablePaymentMethodType> {
        return methods.map { method ->
            if (method.name != PaymentMethodType.CARD.value) return@map method
            val schemes = method.cardSchemes
            if (schemes.isNullOrEmpty()) return@map method
            method.copy(cardSchemes = schemes.withMaestroIfMasterCard())
        }
    }

    private fun filterPaymentConsentsBySession(
        session: AirwallexSession,
        paymentMethodList: List<AvailablePaymentMethodType>,
        paymentConsentList: List<PaymentConsent>
    ): List<PaymentConsent> {
        val cardPaymentMethod = paymentMethodList.find { it.name == PaymentMethodType.CARD.value }
        return if (cardPaymentMethod != null && session !is AirwallexRecurringSession) {
            paymentConsentList.filter { it.paymentMethod?.type == PaymentMethodType.CARD.value }
        } else {
            emptyList()
        }
    }

    private suspend fun <T> loadPagedItems(
        loadPage: suspend (Int) -> Page<T>,
        items: MutableList<T> = Collections.synchronizedList(mutableListOf()),
        pageNum: AtomicInteger = AtomicInteger(0)
    ): List<T> {
        val response = loadPage(pageNum.get())
        pageNum.incrementAndGet()
        items.addAll(response.items)
        return if (response.hasMore) {
            loadPagedItems(
                loadPage,
                items,
                pageNum,
            )
        } else {
            items
        }
    }

    private fun needRequestConsent(session: AirwallexSession): Boolean {
        // if the customerId is null or empty ,there is no need to request consents
        if (session.customerId.isNullOrEmpty()) return false
        if (session is AirwallexRecurringSession) return false
        // if payment methods is not empty and does not contain CARD, no need to request consents
        val paymentMethods = session.paymentMethods
        if (!paymentMethods.isNullOrEmpty() && !paymentMethods.contains(PaymentMethodType.CARD.value)) return false
        // if user wants to hide consents,there is no need to request consents
        return !session.hidePaymentConsents
    }
}
