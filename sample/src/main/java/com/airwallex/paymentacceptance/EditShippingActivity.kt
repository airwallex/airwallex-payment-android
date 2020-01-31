package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.model.PaymentMethod
import kotlinx.android.synthetic.main.activity_edit_shipping.*

class EditShippingActivity : AppCompatActivity() {

    private var menu: Menu? = null

    companion object {

        const val SHIPPING_DETAIL = "SHIPPING_DETAIL"

        fun startActivityForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(
                Intent(activity, EditShippingActivity::class.java),
                requestCode
            )
        }
    }

    private fun updateMenuStatus() {
        menu?.findItem(R.id.menu_save)?.isEnabled =
            contactWidget.isValidContact && shippingWidget.isValidShipping
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
            updateMenuStatus()
        }

        shippingWidget.shippingChangeCallback = {
            updateMenuStatus()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_save -> {
                actionSave()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(IllegalArgumentException::class)
    private fun actionSave() {
        val contact = contactWidget.contact
        val shipping = PaymentMethod.Billing.Builder()
            .setLastName(contact.lastName)
            .setFirstName(contact.firstName)
            .setPhone(contact.phone)
            .setEmail(contact.email)
            .setAddress(shippingWidget.shipping)
            .build()

        val intent = Intent()
        intent.putExtra(SHIPPING_DETAIL, shipping)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}