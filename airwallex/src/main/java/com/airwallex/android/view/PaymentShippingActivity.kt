package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.airwallex.android.databinding.ActivityAddShippingBinding
import com.airwallex.android.R
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.ui.AirwallexActivity

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

    override fun onBackButtonPressed() {
        setResult(RESULT_CANCELED)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        args.shipping?.let {
            viewBinding.shippingWidget.initializeView(it)
        }

        viewBinding.shippingWidget.shippingChangeCallback = {
            viewBinding.btnSaveShipping.isEnabled = viewBinding.shippingWidget.isValid
        }

        viewBinding.btnSaveShipping.setOnSingleClickListener {
            onSaveShipping()
        }
    }

    private fun onSaveShipping() {
        val shipping = viewBinding.shippingWidget.shipping
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
