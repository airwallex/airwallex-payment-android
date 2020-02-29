package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import com.airwallex.android.CustomerSessionConfig
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.view.PaymentCheckoutActivityStarter.Args
import kotlinx.android.parcel.Parcelize

internal class PaymentCheckoutActivityStarter constructor(
    activity: Activity
) : ActivityStarter<PaymentCheckoutActivity, Args>(
    activity,
    PaymentCheckoutActivity::class.java,
    REQUEST_CODE
) {

    @Parcelize
    data class Args internal constructor(
        val customerSessionConfig: CustomerSessionConfig,
        val paymentMethod: PaymentMethod?
    ) : ActivityStarter.Args {

        class Builder(private val customerSessionConfig: CustomerSessionConfig) :
            ObjectBuilder<Args> {
            private var paymentMethod: PaymentMethod? = null


            fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder = apply {
                this.paymentMethod = paymentMethod
            }

            override fun build(): Args {
                return Args(
                    customerSessionConfig = customerSessionConfig,
                    paymentMethod = paymentMethod
                )
            }
        }

        internal companion object {
            internal fun getExtra(intent: Intent): Args {
                return requireNotNull(intent.getParcelableExtra(ActivityStarter.Args.AIRWALLEX_EXTRA))
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1002
    }

}