package com.airwallex.paymentacceptance.viewmodel.base

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.model.Page
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.paymentacceptance.repo.BaseRepository
import com.airwallex.paymentacceptance.repo.LocalMockRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseViewModel : ViewModel() {

    private val _createPaymentIntentError = MutableLiveData<String>()
    val createPaymentIntentError: LiveData<String> = _createPaymentIntentError

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is HttpException) {
            _createPaymentIntentError.postValue(
                throwable.response()?.errorBody()?.string() ?: throwable.localizedMessage
            )
        } else {
            _createPaymentIntentError.postValue(throwable.localizedMessage)
        }
    }

    abstract fun init(activity: ComponentActivity)

    private val repository: BaseRepository by lazy {
        LocalMockRepository()
    }

    /**
     * this method demonstrates how to log in using an apiKey and clientId.
     * this process should be completed on your own server;
     * do not copy this method.
     */
    private suspend fun login() {
        repository.login()
    }

    /**
     * this method demonstrates how to retrieve a paymentIntent from the server.
     * it is only a prerequisite method for initiating the payment flow in the demo.
     * do not copy this method;instead, obtain the paymentIntent from your own server.
     */
    suspend fun getPaymentIntentFromServer(
        force3DS: Boolean = false,
        customerId: String? = null
    ): PaymentIntent {
        return repository.getPaymentIntentFromServer(force3DS, customerId)
    }

    /**
     * this method demonstrates how to retrieve a customerId from the server.
     * it is only a prerequisite method for initiating the payment flow in the demo.
     * do not copy this method;instead, obtain the customerId from your own server.
     */
    suspend fun getCustomerIdFromServer(): String {
        return repository.getCustomerIdFromServer()
    }

    /**
     * this method demonstrates how to retrieve a clientSecret from the server.
     * it is only a prerequisite method for initiating the payment flow in the demo.
     * do not copy this method;instead, obtain the clientSecret from your own server.
     */
    suspend fun getClientSecretFromServer(customerId: String): String {
        return repository.getClientSecretFromServer(customerId)
    }

    suspend fun <T> loadPagedItems(
        items: MutableList<T> = Collections.synchronizedList(mutableListOf()),
        pageNum: AtomicInteger = AtomicInteger(0),
        loadPage: suspend (Int) -> Page<T>
    ): List<T> {
        val response = loadPage(pageNum.get())
        pageNum.incrementAndGet()
        items.addAll(response.items)
        return if (response.hasMore) {
            loadPagedItems(items, pageNum, loadPage)
        } else {
            items
        }
    }

    internal fun getClientSecretFromSession(session: AirwallexSession): String {
        return when (session) {
            is AirwallexPaymentSession -> session.paymentIntent.clientSecret ?: ""
            is AirwallexRecurringWithIntentSession -> session.paymentIntent.clientSecret ?: ""
            is AirwallexRecurringSession -> session.clientSecret
            else -> ""
        }
    }

    fun run(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
            block.invoke()
        }
    }
}