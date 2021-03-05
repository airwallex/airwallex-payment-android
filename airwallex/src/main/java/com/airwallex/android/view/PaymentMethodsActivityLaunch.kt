package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.support.v4.app.Fragment
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.PaymentMethodType
import com.airwallex.android.view.PaymentMethodsActivityLaunch.Args
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

internal class PaymentMethodsActivityLaunch : AirwallexActivityLaunch<PaymentMethodsActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        PaymentMethodsActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        PaymentMethodsActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    data class Args internal constructor(
        val paymentIntent: PaymentIntent,
        val includeCheckoutFlow: Boolean
    ) : AirwallexActivityLaunch.Args {

        class Builder : ObjectBuilder<Args> {
            private lateinit var paymentIntent: PaymentIntent
            private var includeCheckoutFlow: Boolean = true

            fun setPaymentIntent(paymentIntent: PaymentIntent): Builder = apply {
                this.paymentIntent = paymentIntent
            }

            fun setIncludeCheckoutFlow(includeCheckoutFlow: Boolean): Builder = apply {
                this.includeCheckoutFlow = includeCheckoutFlow
            }

            override fun build(): Args {
                return Args(
                    paymentIntent = paymentIntent,
                    includeCheckoutFlow = includeCheckoutFlow
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
        val paymentIntent: PaymentIntent? = null,
        val paymentMethodType: PaymentMethodType? = null,
        var exception: Exception? = null,
        val paymentMethod: PaymentMethod? = null,
        val cvc: String? = null,
        val includeCheckoutFlow: Boolean = false
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            return Bundle().also {
                it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            }
        }

        internal companion object : Parceler<Result> {
            override fun create(parcel: Parcel): Result {
                return Result(
                    paymentIntent = parcel.readParcelable(PaymentIntent::class.java.classLoader),
                    paymentMethodType = parcel.readParcelable(PaymentMethodType::class.java.classLoader),
                    exception = parcel.readSerializable() as? AirwallexException?,
                    paymentMethod = parcel.readParcelable(PaymentMethod::class.java.classLoader),
                    cvc = parcel.readString(),
                    includeCheckoutFlow = parcel.readInt() == 1
                )
            }

            override fun Result.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(paymentIntent, 0)
                parcel.writeParcelable(paymentMethodType, 0)
                parcel.writeSerializable(exception)
                parcel.writeParcelable(paymentMethod, 0)
                parcel.writeString(cvc)
                parcel.writeInt(if (includeCheckoutFlow) 1 else 0)
            }

            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1001
    }
}
