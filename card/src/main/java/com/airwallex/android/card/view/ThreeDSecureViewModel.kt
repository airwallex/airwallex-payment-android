package com.airwallex.android.card.view

import android.app.Activity
import android.app.Application
import androidx.lifecycle.*
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse

class ThreeDSecureViewModel(
    application: Application
) : AndroidViewModel(application) {

    fun continueThreeDSecure(
        transactionId: String?,
        payload: String?,
        activity: Activity
    ): LiveData<ThreeDSecureResult> {
        val resultData = MutableLiveData<ThreeDSecureResult>()

        Cardinal.getInstance().cca_continue(
            transactionId,
            payload,
            activity
        ) { _, validateResponse, jwt ->
            resultData.value = ThreeDSecureResult.Complete(validateResponse, jwt)
        }
        return resultData
    }

    sealed class ThreeDSecureResult {
        data class Complete(val validateResponse: ValidateResponse, val jwt: String) :
            ThreeDSecureResult()
    }

    internal class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ThreeDSecureViewModel(
                application
            ) as T
        }
    }
}
