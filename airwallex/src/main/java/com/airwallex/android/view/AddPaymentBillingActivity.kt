package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.airwallex.android.R
import com.airwallex.android.model.PaymentMethod
import kotlinx.android.synthetic.main.activity_add_billing.*
import kotlinx.android.synthetic.main.activity_airwallex.*

class AddPaymentBillingActivity : AirwallexActivity() {

    private val sameAsShipping: Boolean by lazy {
        intent.getBooleanExtra(SAME_AS_SHIPPING, true)
    }

    private val billing: PaymentMethod.Billing? by lazy {
        intent.getParcelableExtra(PAYMENT_BILLING) as? PaymentMethod.Billing
    }

    companion object {
        fun startActivityForResult(
            activity: Activity,
            billing: PaymentMethod.Billing?,
            sameAsShipping: Boolean
        ) {
            activity.startActivityForResult(
                Intent(activity, AddPaymentBillingActivity::class.java)
                    .putExtra(PAYMENT_BILLING, billing)
                    .putExtra(SAME_AS_SHIPPING, sameAsShipping),
                REQUEST_ADD_BILLING_CODE
            )
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_save)?.isEnabled = isValid()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.airwallex_menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_save) {
            onActionSave()
            true
        } else {
            val handled = super.onOptionsItemSelected(item)
            if (!handled) {
                onBackPressed()
            }
            handled
        }
    }

    override fun onActionSave() {
        setResult(
            Activity.RESULT_OK,
            Intent()
                .putExtra(PAYMENT_BILLING, billingWidget.billing)
                .putExtra(SAME_AS_SHIPPING, billingWidget.sameAsShipping)
        )
        finish()
    }

    override fun isValid(): Boolean {
        return billingWidget.isValid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_add_billing
        viewStub.inflate()
        if (!sameAsShipping) {
            billingWidget.billing = billing
        }
        billingWidget.sameAsShipping = sameAsShipping
        billingWidget.billingChangeCallback = { invalidateOptionsMenu() }
    }
}