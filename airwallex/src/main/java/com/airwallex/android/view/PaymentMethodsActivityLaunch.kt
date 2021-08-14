package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.*
import com.airwallex.android.ui.AirwallexActivityLaunch
import com.airwallex.android.view.PaymentMethodsActivityLaunch.Args
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

class PaymentMethodsActivityLaunch : AirwallexActivityLaunch<PaymentMethodsActivity, Args> {

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
        val session: AirwallexSession
    ) : AirwallexActivityLaunch.Args {

        class Builder : ObjectBuilder<Args> {
            private lateinit var session: AirwallexSession

            fun setAirwallexSession(session: AirwallexSession): Builder = apply {
                this.session = session
            }

            override fun build(): Args {
                return Args(
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
        val exception: AirwallexException? = null,
        val paymentMethod: PaymentMethod? = null,
        val paymentConsentId: String? = null,
        val cvc: String? = null,
        val weChat: WeChat? = null,
        val redirectUrl: String? = null
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
                    paymentConsentId = parcel.readString(),
                    cvc = parcel.readString(),
                    weChat = parcel.readParcelable(WeChat::class.java.classLoader),
                    redirectUrl = parcel.readString()
                )
            }

            override fun Result.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(paymentIntent, 0)
                parcel.writeParcelable(paymentMethodType, 0)
                parcel.writeSerializable(exception)
                parcel.writeParcelable(paymentMethod, 0)
                parcel.writeString(paymentConsentId)
                parcel.writeString(cvc)
                parcel.writeParcelable(weChat, 0)
                parcel.writeString(redirectUrl)
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
