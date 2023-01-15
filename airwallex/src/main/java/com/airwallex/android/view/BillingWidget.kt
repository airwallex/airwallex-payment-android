package com.airwallex.android.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.Billing
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.databinding.WidgetBillingBinding

/**
 * A widget used to collect the [Billing] info
 */
class BillingWidget(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val viewBinding = WidgetBillingBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val sameAsShippingLabel = viewBinding.sameAsShippingLabel
    private val sameAsShippingSwitch = viewBinding.swSameAsShipping
    private val shippingWidget = viewBinding.shippingWidget

    var billingChangeCallback: () -> Unit = {}
        set(value) {
            field = value
            shippingWidget.shippingChangeCallback = value
        }

    private val keyboardController: KeyboardController? by lazy {
        if (context is Activity) {
            KeyboardController(context)
        } else {
            null
        }
    }

    /**
     * Update UI via [Shipping]
     */
    var shipping: Shipping? = null
        set(value) {
            field = value

            if (value != null) {
                shippingWidget.initializeView(value)

                sameAsShippingLabel.visibility = View.VISIBLE
                sameAsShippingSwitch.visibility = View.VISIBLE
                sameAsShippingSwitch.isChecked = true
            } else {
                sameAsShippingLabel.visibility = View.GONE
                sameAsShippingSwitch.visibility = View.GONE
                sameAsShippingSwitch.isChecked = false
            }
        }

    /**
     * Whether to use the same [Address] as in [Shipping]
     */
    private val sameAsShipping: Boolean
        get() {
            return sameAsShippingSwitch.isChecked
        }

    /**
     * Return [Billing] based on user input if valid, otherwise null.
     */
    val billing: Billing?
        get() {
            val shipping = this.shipping
            return if (sameAsShipping && shipping != null) {
                buildBillingWithShipping(shipping)
            } else if (!sameAsShipping && shippingWidget.isValid) {
                buildBillingWithShipping(shippingWidget.shipping)
            } else {
                null
            }
        }

    private fun buildBillingWithShipping(shipping: Shipping): Billing {
        return Billing.Builder()
            .setFirstName(shipping.firstName)
            .setLastName(shipping.lastName)
            .setPhone(shipping.phoneNumber)
            .setEmail(shipping.email)
            .setAddress(
                shipping.address?.apply {
                    Address.Builder()
                        .setCountryCode(countryCode)
                        .setState(state)
                        .setCity(city)
                        .setStreet(street)
                        .setPostcode(postcode)
                        .build()
                }
            )
            .build()
    }

    /**
     * Check if billing is valid
     */
    val isValid: Boolean
        get() {
            return sameAsShipping || !sameAsShipping && shippingWidget.isValid
        }

    init {
        sameAsShippingSwitch.setOnCheckedChangeListener { _, isChecked ->
            shippingWidget.visibility = if (isChecked) View.GONE else View.VISIBLE
            keyboardController?.hide()
            billingChangeCallback.invoke()
        }
    }
}
