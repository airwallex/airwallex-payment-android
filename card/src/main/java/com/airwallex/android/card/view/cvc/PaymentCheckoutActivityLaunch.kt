package com.airwallex.android.card.view.cvc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.airwallex.android.card.view.cvc.PaymentCheckoutActivityLaunch.Args
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.ObjectBuilder
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.WeChat
import com.airwallex.android.ui.AirwallexActivityLaunch
import com.airwallex.android.ui.extension.getExtraResult
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

class PaymentCheckoutActivityLaunch :
    AirwallexActivityLaunch<PaymentCheckoutActivity, Args> {

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
        val paymentConsentId: String?,
        val cvc: String?
    ) : AirwallexActivityLaunch.Args {

        class Builder : ObjectBuilder<Args> {

            private lateinit var session: AirwallexSession
            private lateinit var paymentMethod: PaymentMethod
            private var paymentConsentId: String? = null
            private var cvc: String? = null

            fun setCvc(cvc: String?): Builder = apply {
                this.cvc = cvc
            }

            fun setPaymentConsentId(paymentConsentId: String?): Builder = apply {
                this.paymentConsentId = paymentConsentId
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
                    paymentConsentId = paymentConsentId,
                    cvc = cvc
                )
            }
        }
    }

    @Parcelize
    internal data class Result internal constructor(
        val paymentIntentId: String? = null,
        val weChat: WeChat? = null,
        val redirectUrl: String? = null,
        val exception: AirwallexException? = null
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            return Bundle().also {
                it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            }
        }

        internal companion object : Parceler<Result> {
            override fun create(parcel: Parcel): Result {
                return Result(
                    paymentIntentId = parcel.readString(),
                    weChat = parcel.readParcelable(WeChat::class.java.classLoader),
                    redirectUrl = parcel.readString(),
                    exception = parcel.readSerializable() as? AirwallexException?
                )
            }

            override fun Result.write(parcel: Parcel, flags: Int) {
                parcel.writeString(paymentIntentId)
                parcel.writeParcelable(weChat, 0)
                parcel.writeString(redirectUrl)
                parcel.writeSerializable(exception)
            }

            fun fromIntent(intent: Intent?): Result? {
                return intent?.getExtraResult()
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1004
    }
}