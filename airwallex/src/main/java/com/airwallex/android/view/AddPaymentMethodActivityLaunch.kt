package com.airwallex.android.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexRecurringSession
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.ParcelableSession
import com.airwallex.android.core.Session
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.extension.convertToSession
import com.airwallex.android.core.model.ObjectBuilder
import com.airwallex.android.ui.AirwallexActivityLaunch
import com.airwallex.android.ui.extension.getExtraResult
import com.airwallex.android.ui.extension.toParcelableSession
import com.airwallex.android.view.AddPaymentMethodActivityLaunch.Args
import com.airwallex.android.view.composables.PaymentElementConfiguration
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
        internal val parcelableSession: ParcelableSession? = null,
        @Suppress("DEPRECATION") internal val recurringSession: AirwallexRecurringSession? = null,
        val configuration: PaymentElementConfiguration.Card,
        val isSinglePaymentMethod: Boolean
    ) : AirwallexActivityLaunch.Args {

        val session: AirwallexSession
            get() = parcelableSession?.toSession()
                ?: recurringSession
                ?: error("No session provided in Args")

        class Builder : ObjectBuilder<Args> {
            private var parcelableSession: ParcelableSession? = null
            @Suppress("DEPRECATION")
            private var recurringSession: AirwallexRecurringSession? = null
            private lateinit var configuration: PaymentElementConfiguration.Card
            private var isSinglePaymentMethod: Boolean = false

            @Suppress("DEPRECATION")
            fun setAirwallexSession(session: AirwallexSession): Builder = apply {
                when (session) {
                    is AirwallexRecurringSession -> this.recurringSession = session
                    is Session -> this.parcelableSession = session.toParcelableSession()
                    is AirwallexPaymentSession -> this.parcelableSession = session.convertToSession().toParcelableSession()
                    is AirwallexRecurringWithIntentSession -> this.parcelableSession = session.convertToSession().toParcelableSession()
                    else -> error("Unknown session type: ${session::class}")
                }
            }

            fun setConfiguration(configuration: PaymentElementConfiguration.Card): Builder = apply {
                this.configuration = configuration
            }

            fun setSinglePaymentMethod(isSinglePaymentMethod: Boolean): Builder = apply {
                this.isSinglePaymentMethod = isSinglePaymentMethod
            }

            override fun build(): Args {
                return Args(
                    parcelableSession = parcelableSession,
                    recurringSession = recurringSession,
                    configuration = configuration,
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
