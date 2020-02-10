package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.airwallex.android.model.Shipping
import kotlinx.android.synthetic.main.activity_edit_shipping.*

class PaymentEditShippingActivity : PaymentBaseActivity() {

    override val inPaymentFlow: Boolean
        get() = true

    companion object {
        fun startActivityForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(
                Intent(activity, PaymentEditShippingActivity::class.java),
                requestCode
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_shipping)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        contactWidget.contactChangeCallback = {
            invalidateOptionsMenu()
        }

        shippingWidget.shippingChangeCallback = {
            invalidateOptionsMenu()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menu_save)?.isEnabled =
            contactWidget.isValidContact && shippingWidget.isValidShipping
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_save) {
            actionSave()
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(IllegalArgumentException::class)
    private fun actionSave() {
        val contact = contactWidget.contact
        val shipping = Shipping.Builder()
            .setLastName(contact.lastName)
            .setFirstName(contact.firstName)
            .setPhone(contact.phone)
            .setEmail(contact.email)
            .setAddress(shippingWidget.address)
            .build()
        setResult(Activity.RESULT_OK, Intent().putExtra(SHIPPING_DETAIL, shipping))
        finish()
    }
}