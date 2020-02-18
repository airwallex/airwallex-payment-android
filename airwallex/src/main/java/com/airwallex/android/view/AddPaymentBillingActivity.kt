package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.airwallex.android.R
import kotlinx.android.synthetic.main.activity_add_billing.*
import kotlinx.android.synthetic.main.activity_airwallex.*

class AddPaymentBillingActivity : AirwallexActivity() {

    private val args: AddPaymentBillingActivityStarter.BillingArgs by lazy {
        AddPaymentBillingActivityStarter.BillingArgs.getExtra(intent)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_save)?.isEnabled = isValid()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.airwallex_menu_save, menu)
        return true
    }

    private fun isValid(): Boolean {
        return billingWidget.isValid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewStub.layoutResource = R.layout.activity_add_billing
        viewStub.inflate()

        billingWidget.billing = args.billing
        billingWidget.billingChangeCallback = { invalidateOptionsMenu() }
    }

    override fun onActionSave() {
        setResult(
            Activity.RESULT_OK, Intent()
                .putExtras(
                    AddPaymentBillingActivityStarter.Result(
                        billingWidget.billing
                    ).toBundle()
                )
        )

        finish()
    }
}