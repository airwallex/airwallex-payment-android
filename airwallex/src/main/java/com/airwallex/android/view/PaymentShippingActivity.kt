package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.DrawableCompat
import com.airwallex.android.R
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.databinding.ActivityAddShippingBinding
import com.airwallex.android.ui.AirwallexActivity
import com.airwallex.android.ui.composables.AirwallexColor
import com.airwallex.android.ui.extension.getExtraArgs

// TODO: Remove it after shipping address logic is refactored, i.e. session supports phone number etc.
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
        intent.getExtraArgs()
    }

    override fun onBackButtonPressed() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun initView() {
        super.initView()
        supportActionBar?.let { actionBar ->
            val upArrow = AppCompatResources.getDrawable(this, homeAsUpIndicatorResId())
            upArrow?.let {
                val wrappedDrawable = DrawableCompat.wrap(it)
                DrawableCompat.setTint(wrappedDrawable, AirwallexColor.iconPrimary().toArgb())
                actionBar.setHomeAsUpIndicator(wrappedDrawable)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupButtonColors()

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

    private fun setupButtonColors() {
        val textColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf()
            ),
            intArrayOf(
                AirwallexColor.textSecondary().toArgb(),
                AirwallexColor.textInverse().toArgb()
            )
        )
        viewBinding.btnSaveShipping.setTextColor(textColorStateList)
        viewBinding.headerTitle.setTextColor(AirwallexColor.textPrimary().toArgb())

        val backgroundDrawable = StateListDrawable()

        val disabledDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(AirwallexColor.borderDecorative().toArgb())
            cornerRadius = 6f * resources.displayMetrics.density
        }
        backgroundDrawable.addState(intArrayOf(-android.R.attr.state_enabled), disabledDrawable)

        val enabledDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(AirwallexColor.theme().toArgb())
            cornerRadius = 6f * resources.displayMetrics.density
        }
        backgroundDrawable.addState(intArrayOf(), enabledDrawable)

        viewBinding.btnSaveShipping.background = backgroundDrawable
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
