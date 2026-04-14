package com.airwallex.paymentacceptance.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.BillingAddressParameters
import com.airwallex.android.core.GooglePayOptions
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.model.AvailablePaymentMethodType
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PaymentMethodType
import com.airwallex.android.core.model.RetrieveAvailablePaymentConsentsParams
import com.airwallex.android.core.model.RetrieveAvailablePaymentMethodParams
import com.airwallex.paymentacceptance.nextTriggerBy
import com.airwallex.paymentacceptance.repo.DemoReturnUrl
import com.airwallex.paymentacceptance.viewmodel.base.BaseViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class APIIntegrationViewModel : BaseViewModel() {
    //create your own Airwallex instance to call the APIs.
    private var airwallex: Airwallex? = null

    private val _paymentMethodList = MutableLiveData<List<AvailablePaymentMethodType>>()
    val paymentMethodList: LiveData<List<AvailablePaymentMethodType>> = _paymentMethodList

    private val _paymentConsentList = MutableLiveData<List<PaymentConsent>>()
    val paymentConsentList: LiveData<List<PaymentConsent>> = _paymentConsentList

    override fun init(activity: ComponentActivity) {
        super.init(activity)
        airwallex = Airwallex(activity)
    }

    /**
     * use the Airwallex instance to perform card payment.
     * @param card to complete this API call, you must provide a Card instance.
     * @param force3DS set force3DS to true to trigger the 3DS process during the payment flow.
     * @param saveCard set saveCard to true to save the card information while making the payment.
     */
    fun startPayWithCardDetail(
        card: PaymentMethod.Card,
        force3DS: Boolean = false,
        saveCard: Boolean = true
    ) {
        launch {
            val session = createSession(force3DS = force3DS)
            startLoading()
            confirmPaymentIntentWithCard(
                session = session,
                card = card,
                saveCard = saveCard
            )
        }
    }

    /**
     * start GooglePay
     */
    fun startGooglePay(force3DS: Boolean = false) = launch {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val googlePayOptions = GooglePayOptions(
            allowedCardAuthMethods = if (force3DS) listOf("PAN_ONLY") else null,
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
            skipReadinessCheck = true
        )
        val session = createSession(force3DS = force3DS, googlePayOptions = googlePayOptions)
        airwallex?.startGooglePay(
            session = session,
            listener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    handlePaymentStatus(session, status)
                }
            }
        )
    }

    /**
     * pay by redirection
     *
     * We do not support all redirection payment methods.
     * Before invoking this method, please ensure that the paymentType you provide is one that we support.
     * Below are some of the methods we currently support.
     * 'poli', 'fpx', 'online_banking', 'permatanet', 'bank_transfer', 'alfamart', 'indomaret', 'doku_ewallet', 'enets', 'payeasy', 'payeasy_atm',
     * 'seven_eleven', 'konbini', 'tesco_lotus', 'grabpay', 'skrill', 'eps', 'giropay', 'ideal', 'multibanco', 'p24', 'sofort', 'trustly', 'bancontact',
     * 'dragonpay', 'blik', 'mybank', 'paybybankapp', 'verkkopankki', 'maxima', 'narvesen', 'paypost', 'perlas_terminals', 'paysafecash', 'paysafecard',
     * 'paysera', 'satispay', 'family_mart', 'hi_life', 'sam_kiosk', 'axs_kiosk', 'bigc', 'esun', 'permata_atm', 'boost', 'shopee_pay', 'paypal', 'payu',
     * 'ovo', 'bitpay', 'atome', 'duit_now', 'pay_now', 'prompt_pay', 'go_pay', 'linkaja', 'jenius_pay', 'klarna', 'spei', 'afterpay'
     * You can check all methods by API reference: https://www.airwallex.com/docs/api#/Payment_Acceptance/Config/_api_v1_pa_config_payment_method_types/get JSON Object field: items.name
     */
    fun startPayByRedirection() = launch {
        val session = createSession()
        startLoading()
        airwallex?.startRedirectPay(
            session = session,
            paymentMethodName = "alipayhk",
            listener = object : Airwallex.PaymentResultListener {
                override fun onCompleted(status: AirwallexPaymentStatus) {
                    handlePaymentStatus(session, status)
                }
            }
        )
    }

    /**
     * use the Airwallex instance to perform card payment.
     * @param paymentConsent to complete this API call, you must provide a PaymentConsent instance.
     */
    fun startPayWithConsent(paymentConsent: PaymentConsent) = launch {
        requireNotNull(paymentConsent.id)
        val session = createSession()
        startLoading()
        paymentConsent.paymentMethod?.let { paymentMethod ->
            airwallex?.checkout(
                session = session,
                paymentMethod = paymentMethod,
                paymentConsent = paymentConsent,
                listener = object : Airwallex.PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        handlePaymentStatus(session, status)
                    }
                }
            )
        }
    }

    /**
     * retrieve the list of payment methods you have.
     */
    fun getPaymentMethodsList() = launch {
        //to perform a Google Pay transaction, you must provide an instance of GooglePayOptions
        val googlePayOptions = GooglePayOptions(
            billingAddressRequired = true,
            billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL)
        )
        val session = createSession(googlePayOptions = googlePayOptions)
        val paymentMethods = loadPagedItems { pageNum ->
            // If you are using Java and cannot directly call the suspend function,
            // you can use the overloaded method `retrieveAvailablePaymentMethods` with a callback.
            // To use, call:
            // retrieveAvailablePaymentMethods(
            //     session = <AirwallexSession>,
            //     params = RetrieveAvailablePaymentMethodParams(<params>),
            //     callback = object : AirwallexCallback<Page<AvailablePaymentMethodType>> {
            //         override fun onSuccess(result: Page<AvailablePaymentMethodType>) {
            //             // Handle success
            //         }
            //         override fun onFailure(error: Throwable) {
            //             // Handle failure
            //         }
            //     }
            // )
            airwallex!!.retrieveAvailablePaymentMethods(
                session = session,
                params = RetrieveAvailablePaymentMethodParams.Builder(
                    clientSecret = session.clientSecret!!,
                    pageNum = pageNum
                )
                    .setActive(true)
                    .setTransactionCurrency(session.currency)
                    .setCountryCode(session.countryCode)
                    .build()
            )
        }
        _paymentMethodList.value = paymentMethods
    }

    /**
     * retrieve your list of saved cards.
     */
    fun getPaymentConsentList() = launch {
        //get the customerId and clientSecret from your server
        //please do not directly copy these two methods!
        val customerId = getCustomerIdFromServer()
        val clientSecret = getClientSecretFromServer(customerId)
        val paymentConsents = loadPagedItems { pageNum ->
            // If you are using Java and cannot directly call the suspend function,
            // you can use the overloaded method `retrieveAvailablePaymentConsents` with a callback.
            // To use, call:
            // retrieveAvailablePaymentConsents(
            //     params = RetrieveAvailablePaymentConsentsParams(<params>),
            //     callback = object : AirwallexCallback<Page<PaymentConsent>> {
            //         override fun onSuccess(result: Page<PaymentConsent>) {
            //             // Handle success
            //         }
            //         override fun onFailure(error: Throwable) {
            //             // Handle failure
            //         }
            //     }
            // )
            airwallex!!.retrieveAvailablePaymentConsents(
                RetrieveAvailablePaymentConsentsParams.Builder(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    pageNum = pageNum
                )
                    .setNextTriggeredBy(nextTriggerBy)
                    .setStatus(PaymentConsent.PaymentConsentStatus.VERIFIED)
                    .build()
            )
        }
        _paymentConsentList.value = paymentConsents.filter { it.paymentMethod?.type == PaymentMethodType.CARD.value}
    }

    private suspend fun confirmPaymentIntentWithCard(
        session: AirwallexSession,
        card: PaymentMethod.Card,
        saveCard: Boolean
    ) {
        suspendCoroutine { continuation ->
            airwallex?.checkout(
                session = session,
                paymentMethod = PaymentMethod.Builder()
                    .setType(PaymentMethodType.CARD.value)
                    .setCard(card)
                    .setBilling(null)
                    .build(),
                cvc = card.cvc,
                saveCard = saveCard,
                listener = object : Airwallex.PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        handlePaymentStatus(session, status)
                        continuation.resume(Unit)
                    }
                }
            )
        }
    }

    /**
     * this method will create different types of Sessions based on the different modes.
     */
    private suspend fun createSession(
        force3DS: Boolean = false,
        googlePayOptions: GooglePayOptions? = null,
    ): AirwallexSession {
        return createSession(
            googlePayOptions = googlePayOptions,
            paymentMethods = listOf(),
            returnUrl = DemoReturnUrl.APIIntegration,
            force3DS = force3DS
        )
    }

}
