package com.airwallex.paymentacceptance

import android.text.TextUtils
import com.airwallex.android.ClientSecretProvider
import com.airwallex.android.ClientSecretUpdateListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class ExampleClientSecretProvider : ClientSecretProvider {

    private val api: Api
        get() {
            if (TextUtils.isEmpty(Settings.baseUrl)) {
                throw IllegalArgumentException("Base url should not be null or empty")
            }
            return ApiFactory(Settings.baseUrl).buildRetrofit().create(Api::class.java)
        }

    private val compositeDisposable = CompositeDisposable()

    override fun createClientSecret(customerId: String, updateListener: ClientSecretUpdateListener) {
        compositeDisposable.add(
            api.createClientSecret(customerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ clientSecret ->
                    try {
                        updateListener.onClientSecretUpdate(customerId, clientSecret)
                    } catch (e: IOException) {
                        updateListener.onClientSecretUpdateFailure(e.message ?: "")
                    }
                }, {
                    updateListener.onClientSecretUpdateFailure(it.message ?: "")
                })
        )
    }
}
