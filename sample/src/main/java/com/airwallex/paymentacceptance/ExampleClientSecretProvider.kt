package com.airwallex.paymentacceptance

import com.airwallex.android.ClientSecretProvider
import com.airwallex.android.ClientSecretUpdateListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class ExampleClientSecretProvider : ClientSecretProvider {

    private val api: Api
        get() {
            return ApiFactory(Settings.baseUrl).buildRetrofit().create(Api::class.java)
        }

    private val compositeDisposable = CompositeDisposable()

    override fun createClientSecret(updateListener: ClientSecretUpdateListener) {
        compositeDisposable.add(
            api.createClientSecret(Settings.cachedCustomerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ clientSecret ->
                    try {
                        updateListener.onClientSecretUpdate(clientSecret)
                    } catch (e: IOException) {
                        updateListener.onClientSecretUpdateFailure(e.message ?: "")
                    }
                }, {
                    updateListener.onClientSecretUpdateFailure(it.message ?: "")
                })
        )
    }
}
