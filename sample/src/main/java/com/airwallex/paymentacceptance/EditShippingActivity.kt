package com.airwallex.paymentacceptance

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.airwallex.android.model.PaymentMethod
import kotlinx.android.synthetic.main.activity_edit_shipping.*

class EditShippingActivity : AppCompatActivity(), TextWatcher {

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
            etLastName.text.isNotEmpty()
                    && etFirstName.text.isNotEmpty()
                    && etEmail.text.isNotEmpty()
                    && editShippingLayout.isValidShipping()
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

        PaymentData.shipping?.apply {
            etLastName.setText(lastName)
            etFirstName.setText(firstName)
            etPhoneNumber.setText(phone)
            etEmail.setText(email)
        }

        etLastName.addTextChangedListener(this)
        etFirstName.addTextChangedListener(this)
        etPhoneNumber.addTextChangedListener(this)
        etEmail.addTextChangedListener(this)

        editShippingLayout.shippingChangeCallback = {
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
        val shipping = PaymentMethod.Billing.Builder()
            .setLastName(etLastName.text.toString())
            .setFirstName(etFirstName.text.toString())
            .setPhone(etPhoneNumber.text.toString())
            .setEmail(etEmail.text.toString())
            .setAddress(editShippingLayout.getEditShipping())
            .build()

        val intent = Intent()
        intent.putExtra(SHIPPING_DETAIL, shipping)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun afterTextChanged(s: Editable?) {
        updateMenuStatus()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}