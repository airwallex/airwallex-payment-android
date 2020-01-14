package com.airwallex.example

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_payment_auth.*


class PaymentAuthActivity : AppCompatActivity() {


    data class Customer(val id: String, val label: String)

    private val customerOptions = listOf(
        Customer(id = "test_customer_02", label = "Customer with saved cards"),
        Customer(id = "test_customer_03", label = "Customer with only one saved card"),
        Customer(id = "test_customer_01", label = "Customer without saved card")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_auth)

        spCustomer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }
        }

        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_list_item_1,
            customerOptions.map { it.label })
        spCustomer.adapter = arrayAdapter

        btnLogin.setOnClickListener {
            PaymentOrderInfoActivity.start(this)
        }

        btnContinueAsGuest.setOnClickListener {

        }
    }
}
