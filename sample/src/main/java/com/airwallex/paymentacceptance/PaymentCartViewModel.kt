package com.airwallex.paymentacceptance

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.airwallex.android.AirwallexAddPaymentDialog
import com.airwallex.android.AirwallexStarter
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.AirwallexShippingStatus
import com.airwallex.android.core.model.Shipping

internal class PaymentCartViewModel(
    application: Application
) : AndroidViewModel(application) {

    fun presentShippingFlow(fragment: Fragment, shipping: Shipping?): LiveData<AirwallexShippingStatus> {
        val resultData = MutableLiveData<AirwallexShippingStatus>()
        AirwallexStarter.presentShippingFlow(
            fragment,
            shipping,
            object : Airwallex.ShippingResultListener {
                override fun onCompleted(status: AirwallexShippingStatus) {
                    resultData.value = status
                }
            }
        )
        return resultData
    }

    fun presentEntirePaymentFlow(
        fragment: Fragment,
        session: AirwallexSession,
    ): LiveData<AirwallexPaymentStatus> {
        val resultData = MutableLiveData<AirwallexPaymentStatus>()
        AirwallexStarter.presentPaymentFlow(
            fragment,
            session,
            object : Airwallex.PaymentResultListener {

                override fun onCompleted(status: AirwallexPaymentStatus) {
                    resultData.value = status
                }
            }
        )
        return resultData
    }

    fun presentCardPaymentFlow(
        activity: ComponentActivity,
        session: AirwallexSession,
        dialogMode: Boolean = true
    ): LiveData<AirwallexPaymentStatus> {
        val resultData = MutableLiveData<AirwallexPaymentStatus>()
        if (dialogMode) {
            val dialog = AirwallexAddPaymentDialog(
                activity = activity,
                session = session,
                paymentResultListener = object : Airwallex.PaymentResultListener {
                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        resultData.value = status
                    }
                }
            )
            dialog.show()
        } else {
            AirwallexStarter.presentCardPaymentFlow(
                activity,
                session,
                paymentResultListener = object : Airwallex.PaymentResultListener {

                    override fun onCompleted(status: AirwallexPaymentStatus) {
                        resultData.value = status
                    }
                }
            )
        }
        return resultData
    }

    internal class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PaymentCartViewModel(
                application
            ) as T
        }
    }
}
