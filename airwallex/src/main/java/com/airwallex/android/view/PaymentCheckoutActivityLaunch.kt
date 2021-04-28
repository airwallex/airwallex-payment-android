package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.airwallex.android.AirwallexSession
import com.airwallex.android.exception.AirwallexException
import com.airwallex.android.model.ObjectBuilder
import com.airwallex.android.model.PaymentIntent
import com.airwallex.android.model.PaymentMethod
import com.airwallex.android.model.WeChat
import com.airwallex.android.view.PaymentCheckoutActivityLaunch.Args
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

internal class PaymentCheckoutActivityLaunch : AirwallexActivityLaunch<PaymentCheckoutActivity, Args> {

    constructor(activity: Activity) : super(
        activity,
        PaymentCheckoutActivity::class.java,
        REQUEST_CODE
    )

    constructor(fragment: Fragment) : super(
        fragment,
        PaymentCheckoutActivity::class.java,
        REQUEST_CODE
    )

    @Parcelize
    data class Args internal constructor(
        val session: AirwallexSession,
        val paymentMethod: PaymentMethod,
        val cvc: String?
    ) : AirwallexActivityLaunch.Args {

        class Builder : ObjectBuilder<Args> {

            private lateinit var session: AirwallexSession
            private lateinit var paymentMethod: PaymentMethod
            private var cvc: String? = null

            fun setCvc(cvc: String?): Builder = apply {
                this.cvc = cvc
            }

            fun setAirwallexSession(session: AirwallexSession): Builder = apply {
                this.session = session
            }

            fun setPaymentMethod(paymentMethod: PaymentMethod): Builder = apply {
                this.paymentMethod = paymentMethod
            }

            override fun build(): Args {
                return Args(
                    session = session,
                    paymentMethod = paymentMethod,
                    cvc = cvc
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
        val weChat: WeChat? = null,
        val redirectUrl: String? = null,
        var exception: Exception? = null
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
                    weChat = parcel.readParcelable(WeChat::class.java.classLoader),
                    redirectUrl = parcel.readString(),
                    exception = parcel.readSerializable() as? AirwallexException?
                )
            }

            override fun Result.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(paymentIntent, 0)
                parcel.writeParcelable(weChat, 0)
                parcel.writeString(redirectUrl)
                parcel.writeSerializable(exception)
            }

            fun fromIntent(intent: Intent?): Result? {
                return intent?.getParcelableExtra(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA)
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1004
    }
}
