package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.Shipping
import com.airwallex.android.view.AddPaymentMethodActivityStarter.Args
import kotlinx.android.parcel.Parcelize

internal class AddPaymentMethodActivityStarter constructor(
    activity: Activity
) : ActivityStarter<AddPaymentMethodActivity, Args>(
    activity,
    AddPaymentMethodActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    internal data class Args internal constructor(
        val shipping: Shipping?,
        val customerId: String,
        val clientSecret: String,
        val token: String
    ) : ActivityStarter.Args {

        class Builder : ObjectBuilder<Args> {
            private lateinit var customerId: String
            private lateinit var clientSecret: String
            private lateinit var token: String
            private var shipping: Shipping? = null

            fun setShipping(shipping: Shipping?): Builder = apply {
                this.shipping = shipping
            }

            fun setCustomerId(customerId: String): Builder = apply {
                this.customerId = customerId
            }

            fun setClientSecret(clientSecret: String): Builder = apply {
                this.clientSecret = clientSecret
            }

            fun setToken(token: String): Builder = apply {
                this.token = token
            }

            override fun build(): Args {
                return Args(
                    shipping = shipping,
                    customerId = customerId,
                    clientSecret = clientSecret,
                    token = token
                )
            }
        }

        internal companion object {
            internal fun getExtra(intent: Intent): Args {
                return requireNotNull(intent.getParcelableExtra(ActivityStarter.Args.AIRWALLEX_EXTRA))
            }
        }
    }

    @Parcelize
    internal data class Result internal constructor(
        val paymentMethod: PaymentMethod,
        val cvc: String
    ) : ActivityStarter.Result {
        override fun toBundle(): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(ActivityStarter.Result.AIRWALLEX_EXTRA, this)
            return bundle
        }

        companion object {
            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(ActivityStarter.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1002
    }
}
