package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.airwallex.android.AirwallexCheckoutMode
import com.airwallex.android.AirwallexNextTriggerBy
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.Shipping
import com.airwallex.android.view.AddPaymentMethodActivityLaunch.Args
import kotlinx.android.parcel.Parcelize

internal class AddPaymentMethodActivityLaunch : AirwallexActivityLaunch<AddPaymentMethodActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        AddPaymentMethodActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        AddPaymentMethodActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    data class Args internal constructor(
        val shipping: Shipping?,
        val customerId: String,
        val clientSecret: String,
        val checkoutMode: AirwallexCheckoutMode,
        val nextTriggerBy: AirwallexNextTriggerBy
    ) : AirwallexActivityLaunch.Args {

        class Builder : ObjectBuilder<Args> {
            private lateinit var customerId: String
            private lateinit var clientSecret: String
            private var shipping: Shipping? = null
            private var checkoutMode: AirwallexCheckoutMode = AirwallexCheckoutMode.ONEOFF
            private var nextTriggerBy: AirwallexNextTriggerBy = AirwallexNextTriggerBy.MERCHANT

            fun setShipping(shipping: Shipping?): Builder = apply {
                this.shipping = shipping
            }

            fun setCustomerId(customerId: String): Builder = apply {
                this.customerId = customerId
            }

            fun setClientSecret(clientSecret: String): Builder = apply {
                this.clientSecret = clientSecret
            }

            fun setCheckoutMode(checkoutMode: AirwallexCheckoutMode): Builder = apply {
                this.checkoutMode = checkoutMode
            }

            fun setNextTriggerBy(nextTriggerBy: AirwallexNextTriggerBy): Builder = apply {
                this.nextTriggerBy = nextTriggerBy
            }

            override fun build(): Args {
                return Args(
                    shipping = shipping,
                    customerId = customerId,
                    clientSecret = clientSecret,
                    checkoutMode = checkoutMode,
                    nextTriggerBy = nextTriggerBy
                )
            }
        }

        internal companion object {
            internal fun getExtra(intent: Intent): Args {
                return requireNotNull(intent.getParcelableExtra(AirwallexActivityLaunch.Args.AIRWALLEX_EXTRA))
            }
        }
    }

    @Parcelize
    internal data class Result internal constructor(
        val paymentMethod: PaymentMethod,
        val cvc: String
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            return bundle
        }

        companion object {
            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1002
    }
}
