package com.airwallex.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.example.model.Shipping
import com.airwallex.example.model.ShippingAddress
import com.airwallex.example.model.ShippingContact
import kotlinx.android.synthetic.main.activity_payment_order_info.*

class PaymentOrderInfoActivity : AppCompatActivity() {

    private val shipping = Shipping(
        shippingMethod = "Space-X Rockets",
        firstName = "Yima",
        lastName = "Dangxian",
        phone = "8617601215499",
        shippingAddress = ShippingAddress(
            countryCode = "CN",
            state = "Shanghai",
            city = "Shanghai",
            street = "Shanghai, Shanghai, China",
            postcode = "20000"
        )
    )

    private val shippingContact = ShippingContact(
        email = "yimadangxian@aircross.com",
        phone = "8617601215499"
    )

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, PaymentOrderInfoActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_order_info)


        btnCheckout.setOnClickListener {
            PaymentMethodsActivity.start(this)
        }

        with(shipping) {
            etFirstName.setText(firstName)
            etLastName.setText(lastName)
            etStreetAddress.setText(shippingAddress.street)
            etZipCode.setText(shippingAddress.postcode)
            etCity.setText(shippingAddress.city)
            etState.setText(shippingAddress.state)
            etCountry.setText(shippingAddress.countryCode)
            etPhoneNumber.setText(phone)
        }

        with(shippingContact) {
            etContactEmail.setText(email)
            etContactPhoneNumber.setText(phone)
        }
    }
}