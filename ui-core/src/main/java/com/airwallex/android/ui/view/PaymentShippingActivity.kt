package com.airwallex.android.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.ViewGroup
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.ui.R
import com.airwallex.android.ui.databinding.ActivityAddShippingBinding

/**
 * Activity to edit shipping address
 */
class PaymentShippingActivity : AirwallexActivity() {

    private val viewBinding: ActivityAddShippingBinding by lazy {
        viewStub.layoutResource = R.layout.activity_add_shipping
        val root = viewStub.inflate() as ViewGroup
        ActivityAddShippingBinding.bind(root)
    }

    private val args: PaymentShippingActivityLaunch.Args by lazy {
        PaymentShippingActivityLaunch.Args.getExtra(intent)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_save)?.isEnabled =
            viewBinding.contactWidget.isValidContact && viewBinding.shippingWidget.isValidShipping
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.airwallex_menu_save, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        args.shipping?.let {
            viewBinding.contactWidget.initializeView(it)
            viewBinding.shippingWidget.initializeView(it)
        }

        viewBinding.contactWidget.contactChangeCallback = {
            invalidateOptionsMenu()
        }

        viewBinding.shippingWidget.shippingChangeCallback = {
            invalidateOptionsMenu()
        }
    }

    override fun onActionSave() {
        val contact = viewBinding.contactWidget.shippingContact
        val shipping = Shipping.Builder()
            .setLastName(contact.first)
            .setFirstName(contact.second)
            .setPhone(contact.third)
            .setAddress(viewBinding.shippingWidget.address)
            .build()
        setResult(
            Activity.RESULT_OK,
            Intent().putExtras(
                PaymentShippingActivityLaunch.Result(
                    shipping = shipping
                ).toBundle()
            )
        )
        finish()
    }

    override fun homeAsUpIndicatorResId(): Int {
        return R.drawable.airwallex_ic_back
    }
}
