package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_payment_edit_shipping_addreess.*

class EditShippingAddressActivity : AppCompatActivity() {

    private val compositeSubscription = CompositeDisposable()


    companion object {

        val TAG = EditShippingAddressActivity::class.java.canonicalName

        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, EditShippingAddressActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_edit_shipping_addreess)

        with(TestData.shipping) {
            etFirstName.setText(firstName)
            etLastName.setText(lastName)
            etStreetAddress.setText(shippingAddress.street)
            etZipCode.setText(shippingAddress.postcode)
            etCity.setText(shippingAddress.city)
            etState.setText(shippingAddress.state)
            etCountry.setText(shippingAddress.countryCode)
            etPhoneNumber.setText(phone)
        }
    }


    override fun onDestroy() {
        compositeSubscription.dispose()
        super.onDestroy()
    }
}