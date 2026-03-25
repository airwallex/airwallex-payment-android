package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.AirwallexSupportedCard
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.ObjectBuilder
import com.airwallex.android.ui.AirwallexActivityLaunch
import com.airwallex.android.ui.extension.getExtraResult
import com.airwallex.android.view.AddPaymentMethodActivityLaunch.Args
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

internal class AddPaymentMethodActivityLaunch :
    AirwallexActivityLaunch<AddPaymentMethodActivity, Args> {

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
        val session: AirwallexSession,
        val supportedCardBrands: List<AirwallexSupportedCard>,
        val isSinglePaymentMethod: Boolean
    ) : AirwallexActivityLaunch.Args {

        class Builder : ObjectBuilder<Args> {
            private lateinit var session: AirwallexSession
            private lateinit var supportedCardBrands: List<AirwallexSupportedCard>
            private var isSinglePaymentMethod: Boolean = false

            fun setAirwallexSession(session: AirwallexSession): Builder = apply {
                this.session = session
            }

            fun setSupportedCardBrands(supportedCardBrands: List<AirwallexSupportedCard>): Builder = apply {
                this.supportedCardBrands = supportedCardBrands
            }

            fun setSinglePaymentMethod(isSinglePaymentMethod: Boolean): Builder = apply {
                this.isSinglePaymentMethod = isSinglePaymentMethod
            }

            override fun build(): Args {
                return Args(
                    session = session,
                    supportedCardBrands = supportedCardBrands,
                    isSinglePaymentMethod = isSinglePaymentMethod
                )
            }
        }
    }

    @Parcelize
    internal data class Result internal constructor(
        val paymentIntentId: String? = null,
        val consentId: String? = null,
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
                    consentId = parcel.readString(),
                    exception = parcel.readSerializable() as? AirwallexException
                )
            }

            override fun Result.write(parcel: Parcel, flags: Int) {
                parcel.writeString(paymentIntentId)
                parcel.writeString(consentId)
                parcel.writeSerializable(exception)
            }

            fun fromIntent(intent: Intent?): Result? {
                return intent?.getExtraResult()
            }
        }
    }

    @Parcelize
    internal data class CancellationResult internal constructor(
        val isSinglePaymentMethod: Boolean
    ) : AirwallexActivityLaunch.Result {
        override fun toBundle(): Bundle {
            return Bundle().also {
                it.putParcelable(AirwallexActivityLaunch.Result.AIRWALLEX_EXTRA, this)
            }
        }

        internal companion object {

            fun fromIntent(intent: Intent?): CancellationResult? {
                return intent?.getExtraResult()
            }
        }
    }

    companion object {
        const val REQUEST_CODE: Int = 1002
    }
}
