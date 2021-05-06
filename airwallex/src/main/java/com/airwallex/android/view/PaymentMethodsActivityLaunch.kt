package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.airwallex.android.AirwallexSession
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.*
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.view.PaymentMethodsActivityLaunch.Args
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

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
        val includeCheckoutFlow: Boolean,
        val session: AirwallexSession
    ) : AirwallexActivityLaunch.Args {

        class Builder : ObjectBuilder<Args> {
            private lateinit var session: AirwallexSession
            private var includeCheckoutFlow: Boolean = true

            fun setIncludeCheckoutFlow(includeCheckoutFlow: Boolean): Builder = apply {
                this.includeCheckoutFlow = includeCheckoutFlow
            }

            fun setAirwallexSession(session: AirwallexSession): Builder = apply {
                this.session = session
            }

            override fun build(): Args {
                return Args(
                    includeCheckoutFlow = includeCheckoutFlow,
                    session = session
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
        val weChat: WeChat? = null,
        val redirectUrl: String? = null,
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
                    weChat = parcel.readParcelable(WeChat::class.java.classLoader),
                    redirectUrl = parcel.readString(),
                    includeCheckoutFlow = parcel.readInt() == 1
                )
            }

            override fun Result.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(paymentIntent, 0)
                parcel.writeParcelable(paymentMethodType, 0)
                parcel.writeSerializable(exception)
                parcel.writeParcelable(paymentMethod, 0)
                parcel.writeString(cvc)
                parcel.writeParcelable(weChat, 0)
                parcel.writeString(redirectUrl)
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
